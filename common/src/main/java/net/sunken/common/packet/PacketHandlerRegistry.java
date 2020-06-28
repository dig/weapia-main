package net.sunken.common.packet;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Singleton;
import lombok.Getter;
import net.sunken.common.inject.Facet;

import java.util.Map;
import java.util.Set;

@Singleton
public final class PacketHandlerRegistry implements Facet {

    @Getter
    private final Map<Class<? extends Packet>, Set<PacketHandler>> handlers = Maps.newHashMap();

    public <T extends Packet> void registerHandler(Class<T> packetClass, PacketHandler<T> handler) {
        Set<PacketHandler> packetHandlers;
        if (handlers.get(packetClass) != null) {
            packetHandlers = handlers.get(packetClass);
        } else {
            packetHandlers = Sets.newHashSet();
        }

        packetHandlers.add(handler);
        handlers.put(packetClass, packetHandlers);
    }
}
