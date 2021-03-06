package net.sunken.common.packet;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.event.EventManager;
import net.sunken.common.inject.Disableable;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.event.PacketReceivedEvent;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class PacketListener implements Facet, Enableable, Disableable {

    private String packetChannel;
    private RedisConnection redisConnection;
    private PacketHandlerRegistry packetHandlerRegistry;
    private EventManager eventManager;

    private Jedis subscriber;
    private Thread subscriberThread;

    @Inject
    public PacketListener(
            @Named("PacketChannel") String packetChannel,
            RedisConnection redisConnection,
            PacketHandlerRegistry packetHandlerRegistry,
            EventManager eventManager) {
        this.packetChannel = packetChannel;
        this.redisConnection = redisConnection;
        this.packetHandlerRegistry = packetHandlerRegistry;
        this.eventManager = eventManager;
    }

    @Override
    public void enable() {
        subscriber = redisConnection.getConnection();
        subscriberThread = new Thread(() -> subscriber.subscribe(new Listener(), packetChannel.getBytes()));
        subscriberThread.start();
    }

    @Override
    public void disable() {
        subscriberThread.interrupt();
    }

    private class Listener extends BinaryJedisPubSub {

        private Map<Class<? extends Packet>, Set<PacketHandler>> handlers = packetHandlerRegistry.getHandlers();

        @Override
        public void onMessage(byte[] channel, byte[] message) {
            if (Arrays.equals(channel, packetChannel.getBytes())) {
                Packet deserialized = Packet.fromBytes(message);

                if (deserialized != null && handlers.containsKey(deserialized.getClass())) {
                    Set<PacketHandler> packetHandlers = handlers.get(deserialized.getClass());
                    packetHandlers.forEach(handler -> handler.onReceive(deserialized));
                }

                eventManager.callEvent(new PacketReceivedEvent(deserialized));
            }
        }

        @Override
        public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {}
        @Override
        public void onSubscribe(byte[] channel, int subscribedChannels) {}
        @Override
        public void onUnsubscribe(byte[] channel, int subscribedChannels) {}
        @Override
        public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {}
        @Override
        public void onPSubscribe(byte[] pattern, int subscribedChannels) {}
    }
}
