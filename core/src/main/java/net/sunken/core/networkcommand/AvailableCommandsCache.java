package net.sunken.core.networkcommand;

import lombok.*;
import net.sunken.common.database.*;
import net.sunken.common.inject.*;
import net.sunken.common.master.*;
import net.sunken.common.networkcommand.*;
import net.sunken.common.packet.*;
import redis.clients.jedis.*;

import javax.inject.*;
import java.util.*;

@Singleton
public class AvailableCommandsCache extends PacketHandler<MasterRebootPacket> implements Facet, Enableable {

    @Getter
    private Set<String> availableCommmands = new HashSet<>();

    @Inject
    private RedisConnection redisConnection;
    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;

    @Override
    public void onReceive(MasterRebootPacket packet) {
        fetch();
    }

    @Override
    public void enable() {
        fetch();
    }

    private void fetch() {
        try (Jedis connection = redisConnection.getJedisPool().getResource()) {
            availableCommmands = connection.smembers(NetworkCommandConstants.COMMAND_LIST_KEY);
        }

        packetHandlerRegistry.registerHandler(MasterRebootPacket.class, this);
    }

    @Override
    public void disable() {
    }
}
