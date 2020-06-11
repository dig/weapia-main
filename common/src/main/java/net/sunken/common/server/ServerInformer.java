package net.sunken.common.server;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.server.packet.ServerAddPacket;
import net.sunken.common.server.packet.ServerRemovePacket;
import net.sunken.common.server.packet.ServerUpdatePacket;
import redis.clients.jedis.Jedis;

public class ServerInformer {

    @Inject
    private RedisConnection redisConnection;
    @Inject
    private PacketUtil packetUtil;

    public void add(Server server, boolean notify) {
        try (Jedis jedis = redisConnection.getConnection()) {
            ImmutableMap.Builder<String, String> serverKeysBuilder = ImmutableMap.<String, String>builder()
                    .put(ServerHelper.SERVER_ID_KEY, server.getId())
                    .put(ServerHelper.SERVER_TYPE_KEY, server.getType().toString())
                    .put(ServerHelper.SERVER_GAME_KEY, server.getGame().toString())
                    .put(ServerHelper.SERVER_WORLD_KEY, server.getWorld().toString())
                    .put(ServerHelper.SERVER_HOST_KEY, server.getHost())
                    .put(ServerHelper.SERVER_PORT_KEY, String.valueOf(server.getPort()))
                    .put(ServerHelper.SERVER_PLAYERS_KEY, String.valueOf(server.getPlayers()))
                    .put(ServerHelper.SERVER_MAXPLAYERS_KEY, String.valueOf(server.getMaxPlayers()))
                    .put(ServerHelper.SERVER_STATE_KEY, server.getState().toString());

            //--- Metadata
            for (String key : ServerHelper.SERVER_METADATA_KEYS) {
                if (server.getMetadata().containsKey(key)) {
                    serverKeysBuilder.put(key, server.getMetadata().get(key));
                }
            }

            jedis.hmset(ServerHelper.SERVER_STORAGE_KEY + ":" + server.getId(), serverKeysBuilder.build());
        }

        if (notify) {
            packetUtil.send(new ServerAddPacket(server.getId()));
        }
    }

    public void update(Server server, boolean notify) {
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

    public void updateMetadata(Server server, boolean notify) {
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

    public void remove(String id, boolean notify) {
        try (Jedis jedis = redisConnection.getConnection()) {
            jedis.del(ServerHelper.SERVER_STORAGE_KEY + ":" + id);
        }

        if (notify) {
            packetUtil.send(new ServerRemovePacket(id));
        }
    }

}
