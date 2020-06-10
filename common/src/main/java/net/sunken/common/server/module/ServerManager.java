package net.sunken.common.server.module;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.server.*;
import net.sunken.common.server.packet.ServerAddPacket;
import net.sunken.common.server.packet.ServerConnectedPacket;
import net.sunken.common.server.packet.ServerRemovePacket;
import net.sunken.common.server.packet.ServerUpdatePacket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log
@Singleton
public class ServerManager implements Facet, Enableable {

    private final RedisConnection redisConnection;
    private final PacketHandlerRegistry packetHandlerRegistry;

    @Inject
    private ServerAddHandler serverAddHandler;
    @Inject
    private ServerRemoveHandler serverRemoveHandler;
    @Inject
    private ServerUpdateHandler serverUpdateHandler;
    @Inject
    private ServerConnectedHandler serverConnectedHandler;

    @Getter
    private Set<Server> serverList = ImmutableSet.of();
    @Getter
    private Cache<UUID, ServerDetail> pendingPlayerConnection;

    @Inject
    public ServerManager(RedisConnection redisConnection, PacketHandlerRegistry packetHandlerRegistry) {
        this.redisConnection = redisConnection;
        this.packetHandlerRegistry = packetHandlerRegistry;
        this.pendingPlayerConnection = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void enable() {
        try (Jedis jedis = redisConnection.getConnection()) {
            Set<Server> scannedServerList = Sets.newLinkedHashSet();

            ScanParams params = new ScanParams();
            params.count(200);
            params.match(ServerHelper.SERVER_STORAGE_KEY + ":*");

            ScanResult<String> scanResult = jedis.scan("0", params);
            List<String> keys = scanResult.getResult();

            for (String key : keys) {
                Map<String, String> kv = jedis.hgetAll(key);

                Server server = fromRedis(kv);
                scannedServerList.add(server);

                log.info(String.format("Loaded server (%s)", server.toString()));
            }

            serverList = scannedServerList;
        }

        packetHandlerRegistry.registerHandler(ServerAddPacket.class, serverAddHandler);
        packetHandlerRegistry.registerHandler(ServerRemovePacket.class, serverRemoveHandler);
        packetHandlerRegistry.registerHandler(ServerUpdatePacket.class, serverUpdateHandler);
        packetHandlerRegistry.registerHandler(ServerConnectedPacket.class, serverConnectedHandler);
    }

    @Override
    public void disable() {
    }

    public Server fromRedis(Map<String, String> kv) {
        log.info(kv.toString());

        Server.Type type = Server.Type.valueOf(kv.get(ServerHelper.SERVER_TYPE_KEY));
        Game game = Game.valueOf(kv.get(ServerHelper.SERVER_GAME_KEY));

        Map<String, String> metadata = new HashMap<>();
        for (String key : ServerHelper.SERVER_METADATA_KEYS) {
            if (kv.containsKey(key)) {
                metadata.put(key, kv.get(key));
            }
        }

        return Server.builder()
                .id(kv.get(ServerHelper.SERVER_ID_KEY))
                .type(type)
                .host(kv.get(ServerHelper.SERVER_HOST_KEY))
                .port(Integer.parseInt(kv.get(ServerHelper.SERVER_PORT_KEY)))
                .game(game)
                .world(World.valueOf(kv.get(ServerHelper.SERVER_WORLD_KEY)))
                .players(Integer.parseInt(kv.get(ServerHelper.SERVER_PLAYERS_KEY)))
                .maxPlayers(Integer.parseInt(kv.get(ServerHelper.SERVER_MAXPLAYERS_KEY)))
                .state(Server.State.valueOf(kv.get(ServerHelper.SERVER_STATE_KEY)))
                .metadata(metadata)
                .build();
    }

    public Optional<Server> findServerById(@NonNull String id) {
        return serverList.stream()
                .filter(server -> server.getId().equals(id))
                .findFirst();
    }

    public Optional<Server> findAvailable(@NonNull Server.Type type, @NonNull Game game) {
        return serverList.stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .filter(Server::canJoin)
                .filter(server -> (server.getPlayers() + (int) findPendingConnectionCount(server)) < server.getMaxPlayers())
                .findFirst();
    }

    public Optional<Server> findAvailable(@NonNull Server.Type type, @NonNull Game game, @NonNull List<String> excluded) {
        return serverList.stream()
                .filter(server -> !excluded.contains(server.getId()))
                .filter(server -> server.getType() == type && server.getGame() == game)
                .filter(Server::canJoin)
                .filter(server -> (server.getPlayers() + (int) findPendingConnectionCount(server)) < server.getMaxPlayers())
                .findFirst();
    }

    public Optional<Server> findAvailable(@NonNull Server.Type type, @NonNull Game game, int amountNeeded) {
        return serverList.stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .filter(Server::canJoin)
                .filter(server -> ((server.getPlayers() + (int) findPendingConnectionCount(server)) + amountNeeded) <= server.getMaxPlayers())
                .findFirst();
    }

    public Set<Server> findAll(@NonNull Server.Type type, @NonNull Game game) {
        return serverList.stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .collect(Collectors.toSet());
    }

    public long findAllCount(@NonNull Server.Type type, @NonNull Game game) {
        return serverList.stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .count();
    }

    public Set<Server> findAllAvailable(@NonNull Server.Type type, @NonNull Game game) {
        return findAll(type, game).stream()
                .filter(Server::canJoin)
                .filter(server -> (server.getPlayers() + (int) findPendingConnectionCount(server)) < server.getMaxPlayers())
                .collect(Collectors.toSet());
    }

    public long findPendingConnectionCount(@NonNull String id) {
        return pendingPlayerConnection.asMap().values().stream()
                .filter(serverDetail -> serverDetail.getId().equals(id))
                .count();
    }

    public long findPendingConnectionCount(@NonNull ServerDetail serverDetail) {
        return findPendingConnectionCount(serverDetail.getId());
    }

    public long findPendingConnectionCount(@NonNull Server server) {
        return findPendingConnectionCount(server.getId());
    }

    public boolean hasPendingConnection(@NonNull UUID uuid) {
        return pendingPlayerConnection.getIfPresent(uuid) != null;
    }

    public int getPlayersOnline(@NonNull Server.Type type, @NonNull Game game) {
        int playersOnline = 0;

        Set<Server> serversWithType = findAll(type, game);
        for (Server srv : serversWithType) {
            playersOnline += srv.getPlayers();
        }

        return playersOnline;
    }

    public int getTotalPlayersOnline() {
        int totalPlayersOnline = 0;

        Set<Server> allBungees = serverList.stream()
                .filter(srv -> srv.getType() == Server.Type.BUNGEE)
                .collect(Collectors.toSet());

        for (Server srv : allBungees) {
            totalPlayersOnline += srv.getPlayers();
        }

        return totalPlayersOnline;
    }

    public int findNextAvailableID(@NonNull Server.Type type, @NonNull Game game) {
        int assignableID = 0;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            boolean available = true;

            Set<Server> servers = findAll(type, game);
            for (Server server : servers) {
                Map<String, String> serverMetadata = server.getMetadata();
                if (serverMetadata.containsKey(ServerHelper.SERVER_METADATA_ID_KEY)) {
                    int id = Integer.parseInt(serverMetadata.get(ServerHelper.SERVER_METADATA_ID_KEY));

                    if (id == i) {
                        available = false;
                        break;
                    }
                }
            }

            if (available) {
                assignableID = i;
                break;
            }
        }

        return assignableID;
    }

}
