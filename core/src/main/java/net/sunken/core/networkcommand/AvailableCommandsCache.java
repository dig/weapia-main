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
public class AvailableCommandsCache extends PacketHandler<MasterBootPacket> implements Facet, Enableable {

    @Getter
    private Set<String> availableCommands = new HashSet<>();

    @Inject
    private RedisConnection redisConnection;
    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;

    @Override
    public void onReceive(MasterBootPacket packet) {
        fetch();
    }

    @Override
    public void enable() {
        fetch();

        packetHandlerRegistry.registerHandler(MasterBootPacket.class, this);
    }

    private void fetch() {
        try (Jedis connection = redisConnection.getJedisPool().getResource()) {
            availableCommands = connection.smembers(NetworkCommandConstants.COMMAND_LIST_KEY);
        }
    }
}
