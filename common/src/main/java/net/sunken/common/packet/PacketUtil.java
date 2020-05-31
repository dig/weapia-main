package net.sunken.common.packet;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.util.AsyncHelper;
import redis.clients.jedis.Jedis;

import java.util.function.Predicate;

public class PacketUtil {

    private String packetChannel;
    private RedisConnection redisConnection;

    @Inject
    public PacketUtil(
            @Named("PacketChannel") String packetChannel,
            RedisConnection redisConnection) {
        this.packetChannel = packetChannel;
        this.redisConnection = redisConnection;
    }

    public void send(Packet packet) {
        AsyncHelper.executor().submit(() -> {
            Jedis jedis = redisConnection.getConnection();

            try {
                jedis.publish(packetChannel.getBytes(), packet.toBytes());
            } finally {
                jedis.close();
            }
        });
    }

    public void sendSync(Packet packet) {
        Jedis jedis = redisConnection.getConnection();

        try {
            jedis.publish(packetChannel.getBytes(), packet.toBytes());
        } finally {
            jedis.close();
        }
    }

}
