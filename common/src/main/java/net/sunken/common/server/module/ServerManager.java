package net.sunken.common.server.module;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.server.*;
import net.sunken.common.server.packet.ServerAddPacket;
import net.sunken.common.server.packet.ServerConnectedPacket;
import net.sunken.common.server.packet.ServerRemovePacket;
import net.sunken.common.server.packet.ServerUpdatePacket;
import net.sunken.common.util.RedisUtil;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log
@Singleton
public class ServerManager implements Facet, Enableable {

    @Inject
    private RedisConnection redisConnection;
    @Inject
    private PacketUtil packetUtil;

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private ServerAddHandler serverAddHandler;
    @Inject
    private ServerRemoveHandler serverRemoveHandler;
    @Inject
    private ServerUpdateHandler serverUpdateHandler;
    @Inject
    private ServerConnectedHandler serverConnectedHandler;

    @Getter
    private final Cache<UUID, ServerDetail> pendingPlayerConnection;
    private final Map<String, Server> servers = Maps.newConcurrentMap();

    public ServerManager() {
        this.pendingPlayerConnection = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build();
    }

    public void add(@NonNull Server server, boolean local) {
        servers.put(server.getId(), server);

        if (!local) {
            try (Jedis jedis = redisConnection.getConnection()) {
                jedis.hmset(ServerHelper.SERVER_STORAGE_KEY + ":" + server.getId(), server.toRedis());
            }
            packetUtil.sendSync(new ServerAddPacket(server.getId()));
        }
    }

    public void remove(@NonNull String id, boolean local) {
        servers.remove(id);

        if (!local) {
            try (Jedis jedis = redisConnection.getConnection()) {
                jedis.del(ServerHelper.SERVER_STORAGE_KEY + ":" + id);
            }
            packetUtil.sendSync(new ServerRemovePacket(id));
        }
    }

    public void update(@NonNull Server server, boolean notify) {
        try (Jedis jedis = redisConnection.getConnection()) {
            ImmutableMap.Builder<String, String> serverKeysBuilder = ImmutableMap.<String, String>builder()
                    .put(ServerHelper.SERVER_PLAYERS_KEY, String.valueOf(server.getPlayers()))
                    .put(ServerHelper.SERVER_STATE_KEY, server.getState().toString());

            jedis.hmset(ServerHelper.SERVER_STORAGE_KEY + ":" + server.getId(), serverKeysBuilder.build());
        }

        if (notify) {
            packetUtil.send(new ServerUpdatePacket(server.getId(), ServerUpdatePacket.Type.SERVER));
        }
    }

    public void updateMetadata(@NonNull Server server, boolean notify) {
        try (Jedis jedis = redisConnection.getConnection()) {
            ImmutableMap.Builder<String, String> serverKeysBuilder = ImmutableMap.<String, String>builder();

            for (String key : ServerHelper.SERVER_METADATA_KEYS) {
                if (server.getMetadata().containsKey(key)) {
                    serverKeysBuilder.put(key, server.getMetadata().get(key));
                }
            }

            jedis.hmset(ServerHelper.SERVER_STORAGE_KEY + ":" + server.getId(), serverKeysBuilder.build());
        }

        if (notify) {
            packetUtil.send(new ServerUpdatePacket(server.getId(), ServerUpdatePacket.Type.METADATA));
        }
    }

    public Optional<Server> findServerById(@NonNull String id) {
        return Optional.ofNullable(servers.get(id));
    }

    public Optional<Server> findAvailable(@NonNull Server.Type type, @NonNull Game game) {
        return servers.values().stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .filter(Server::canJoin)
                .filter(server -> (server.getPlayers() + (int) findPendingConnectionCount(server)) < server.getMaxPlayers())
                .findFirst();
    }

    public Optional<Server> findAvailable(@NonNull Server.Type type, @NonNull Game game, @NonNull List<String> excluded) {
        return servers.values().stream()
                .filter(server -> !excluded.contains(server.getId()))
                .filter(server -> server.getType() == type && server.getGame() == game)
                .filter(Server::canJoin)
                .filter(server -> (server.getPlayers() + (int) findPendingConnectionCount(server)) < server.getMaxPlayers())
                .findFirst();
    }

    public Optional<Server> findAvailable(@NonNull Server.Type type, @NonNull Game game, int amountNeeded) {
        return servers.values().stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .filter(Server::canJoin)
                .filter(server -> ((server.getPlayers() + (int) findPendingConnectionCount(server)) + amountNeeded) <= server.getMaxPlayers())
                .findFirst();
    }

    public Set<Server> findAll(@NonNull Server.Type type, @NonNull Game game) {
        return servers.values().stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .collect(Collectors.toSet());
    }

    public Collection<Server> findAll() {
        return servers.values();
    }

    public long findAllCount(@NonNull Server.Type type, @NonNull Game game) {
        return servers.values().stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .count();
    }

    public long findAllAvailableCount(@NonNull Server.Type type, @NonNull Game game) {
        return servers.values().stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .filter(Server::canJoin)
                .filter(server -> (server.getPlayers() + (int) findPendingConnectionCount(server)) < server.getMaxPlayers())
                .count();
    }

    public Set<Server> findAllAvailable(@NonNull Server.Type type, @NonNull Game game) {
        return servers.values().stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .filter(Server::canJoin)
                .filter(server -> (server.getPlayers() + (int) findPendingConnectionCount(server)) < server.getMaxPlayers())
                .collect(Collectors.toSet());
    }

    public long findAllPendingCount(@NonNull Server.Type type, @NonNull Game game) {
        return servers.values().stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .filter(server -> server.getState() == Server.State.PENDING)
                .count();
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
        for (Server srv : findAll(Server.Type.BUNGEE, Game.NONE)) {
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

    @Override
    public void enable() {
        try (Jedis jedis = redisConnection.getConnection()) {
            Set<String> keys = RedisUtil.scanAll(jedis, ServerHelper.SERVER_STORAGE_KEY + ":*");
            for (String key : keys) {
                Map<String, String> kv = jedis.hgetAll(key);
                Server server = ServerHelper.from(kv);
                servers.put(server.getId(), server);
            }
        }

        packetHandlerRegistry.registerHandler(ServerAddPacket.class, serverAddHandler);
        packetHandlerRegistry.registerHandler(ServerRemovePacket.class, serverRemoveHandler);
        packetHandlerRegistry.registerHandler(ServerUpdatePacket.class, serverUpdateHandler);
        packetHandlerRegistry.registerHandler(ServerConnectedPacket.class, serverConnectedHandler);
    }

    @Override
    public void disable() {
    }

}
