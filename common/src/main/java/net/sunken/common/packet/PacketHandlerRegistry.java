package net.sunken.common.packet;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import lombok.Getter;
import net.sunken.common.inject.Facet;

import java.util.Map;

@Singleton
public final class PacketHandlerRegistry implements Facet {

    @Getter
    private Map<Class<? extends Packet>, PacketHandler> handlers;

    public PacketHandlerRegistry() {
        handlers = Maps.newHashMap();
    }

    public <T extends Packet> void registerHandler(Class<T> packetClass, PacketHandler<T> handler) {
        handlers.put(packetClass, handler);
    }

}
