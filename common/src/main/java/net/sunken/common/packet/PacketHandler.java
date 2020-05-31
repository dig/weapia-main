package net.sunken.common.packet;

import lombok.Getter;

import java.lang.reflect.ParameterizedType;

public abstract class PacketHandler<T extends Packet> {

    @Getter
    private final Class<T> type;
    public PacketHandler() {
        this.type = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public abstract void onReceive(T packet);

}