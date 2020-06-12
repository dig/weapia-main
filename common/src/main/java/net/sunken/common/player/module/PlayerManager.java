package net.sunken.common.player.module;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.packet.PlayerSaveStatePacket;

import java.util.*;

@Singleton
public class PlayerManager implements Facet, Enableable {

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private PlayerSaveStateHandler playerSaveStateHandler;

    private final Map<UUID, AbstractPlayer> onlinePlayers = Maps.newConcurrentMap();

    @Override
    public void enable() {
        packetHandlerRegistry.registerHandler(PlayerSaveStatePacket.class, playerSaveStateHandler);
    }

    @Override
    public void disable() {
    }

    public void add(@NonNull AbstractPlayer abstractPlayer) {
        onlinePlayers.put(abstractPlayer.getUuid(), abstractPlayer);
    }

    public Optional<AbstractPlayer> get(@NonNull UUID uuid) {
        return Optional.ofNullable(onlinePlayers.get(uuid));
    }

    public void remove(@NonNull UUID uuid) {
        onlinePlayers.remove(uuid);
    }

    public Set<AbstractPlayer> getOnlinePlayers() {
        return Sets.newHashSet(onlinePlayers.values());
    }

}
