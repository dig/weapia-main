package net.sunken.common.player.module;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.packet.PlayerSaveStatePacket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PlayerManager implements Facet, Enableable {

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private PlayerSaveStateHandler playerSaveStateHandler;

    @Getter
    private Set<AbstractPlayer> onlinePlayers;

    public PlayerManager() {
        this.onlinePlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public void enable() {
        packetHandlerRegistry.registerHandler(PlayerSaveStatePacket.class, playerSaveStateHandler);
    }

    @Override
    public void disable() {
    }

    public void add(@NonNull AbstractPlayer abstractPlayer) {
        onlinePlayers.add(abstractPlayer);
    }

    public Optional<AbstractPlayer> get(@NonNull UUID uuid) {
        return onlinePlayers.stream()
                .filter(abstractPlayer -> abstractPlayer.getUuid().equals(uuid))
                .findFirst();
    }

    public void remove(@NonNull UUID uuid) {
        onlinePlayers.removeIf(abstractPlayer -> abstractPlayer.getUuid().equals(uuid));
    }

}
