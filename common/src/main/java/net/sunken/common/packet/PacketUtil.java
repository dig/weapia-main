package net.sunken.common.packet;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.sunken.common.database.RedisConnection;
import redis.clients.jedis.Jedis;

public class PacketUtil {

    private final String packetChannel;
    private final RedisConnection redisConnection;

    @Inject
    public PacketUtil(
            @Named("PacketChannel") String packetChannel,
            RedisConnection redisConnection) {
        this.packetChannel = packetChannel;
        this.redisConnection = redisConnection;
    }

    public void send(Packet packet) {
        Jedis jedis = redisConnection.getConnection();
        try {
            jedis.publish(packetChannel.getBytes(), packet.toBytes());
        } finally {
            jedis.close();
        }
    }
}
