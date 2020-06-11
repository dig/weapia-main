package net.sunken.common.network;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.network.handler.NetworkJoinHandler;
import net.sunken.common.network.handler.NetworkQuitHandler;
import net.sunken.common.network.packet.NetworkJoinPacket;
import net.sunken.common.network.packet.NetworkQuitPacket;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.PlayerDetail;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log
@Singleton
public class NetworkManager implements Facet, Enableable {

    @Inject
    private PacketUtil packetUtil;
    @Inject
    private RedisConnection redisConnection;

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private NetworkJoinHandler networkJoinHandler;
    @Inject
    private NetworkQuitHandler networkQuitHandler;

    private final Map<UUID, PlayerDetail> playerCache = Maps.newConcurrentMap();
    private final Map<String, UUID> nameCache = Maps.newConcurrentMap();

    public void add(@NonNull PlayerDetail playerDetail, boolean local) {
        playerCache.put(playerDetail.getUuid(), playerDetail);
        nameCache.put(playerDetail.getDisplayName(), playerDetail.getUuid());

        if (!local) {
            try (Jedis jedis = redisConnection.getConnection()) {
                jedis.hmset(NetworkHelper.NETWORK_PLAYER_STORAGE_KEY + ":" + playerDetail.getUuid().toString(), playerDetail.toRedis());
            }
            packetUtil.sendSync(new NetworkJoinPacket(playerDetail));
        }
    }

    public void remove(@NonNull PlayerDetail playerDetail, boolean local) {
        playerCache.remove(playerDetail.getUuid());
        nameCache.remove(playerDetail.getDisplayName());

        if (!local) {
            try (Jedis jedis = redisConnection.getConnection()) {
                jedis.del(NetworkHelper.NETWORK_PLAYER_STORAGE_KEY + ":" + playerDetail.getUuid().toString());
            }
            packetUtil.sendSync(new NetworkQuitPacket(playerDetail));
        }
    }

    public Optional<PlayerDetail> get(@NonNull UUID uuid) {
        return Optional.ofNullable(playerCache.get(uuid));
    }

    public Optional<PlayerDetail> get(@NonNull String displayName) {
        UUID uuid = nameCache.get(displayName);
        if (uuid != null) {
            return Optional.ofNullable(playerCache.get(uuid));
        }
        return Optional.empty();
    }

    @Override
    public void enable() {
        try (Jedis jedis = redisConnection.getConnection()) {
            ScanParams params = new ScanParams();
            params.count(200);
            params.match(NetworkHelper.NETWORK_PLAYER_STORAGE_KEY + ":*");

            ScanResult<String> scanResult = jedis.scan("0", params);
            List<String> keys = scanResult.getResult();

            for (String key : keys) {
                Map<String, String> kv = jedis.hgetAll(key);
                PlayerDetail playerDetail = NetworkHelper.from(kv);

                playerCache.put(playerDetail.getUuid(), playerDetail);
                nameCache.put(playerDetail.getDisplayName(), playerDetail.getUuid());

                log.info(String.format("Loaded player %s", playerDetail.toRedis()));
            }
        }

        packetHandlerRegistry.registerHandler(NetworkJoinPacket.class, networkJoinHandler);
        packetHandlerRegistry.registerHandler(NetworkQuitPacket.class, networkQuitHandler);
    }

    @Override
    public void disable() {
    }

}
