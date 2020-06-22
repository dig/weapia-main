package net.sunken.common.player.module;

import com.google.common.collect.Maps;
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

    private final Map<UUID, AbstractPlayer> playerCache = Maps.newConcurrentMap();
    private final Map<String, UUID> nameCache = Maps.newConcurrentMap();

    @Override
    public void enable() {
        packetHandlerRegistry.registerHandler(PlayerSaveStatePacket.class, playerSaveStateHandler);
    }

    @Override
    public void disable() {
    }

    public void add(@NonNull AbstractPlayer abstractPlayer) {
        playerCache.put(abstractPlayer.getUuid(), abstractPlayer);
        nameCache.put(abstractPlayer.getUsername().toLowerCase(), abstractPlayer.getUuid());
    }

    public Optional<AbstractPlayer> get(@NonNull UUID uuid) {
        return Optional.ofNullable(playerCache.get(uuid));
    }

    public Optional<AbstractPlayer> get(@NonNull String name) {
        if (nameCache.containsKey(name.toLowerCase())) {
            return get(nameCache.get(name.toLowerCase()));
        }
        return Optional.empty();
    }

    public void remove(@NonNull UUID uuid) {
        playerCache.remove(uuid);
        nameCache.keySet().forEach(name -> {
            UUID value = nameCache.get(name);
            if (value.equals(uuid)) {
                nameCache.remove(name);
            }
        });
    }

    public Collection<AbstractPlayer> getOnlinePlayers() {
        return playerCache.values();
    }
}
