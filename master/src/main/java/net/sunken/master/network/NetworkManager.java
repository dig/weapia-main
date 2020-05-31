package net.sunken.master.network;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.player.PlayerDetail;
import net.sunken.common.player.packet.PlayerProxyJoinPacket;
import net.sunken.common.player.packet.PlayerProxyQuitPacket;
import net.sunken.master.network.handler.PlayerProxyJoinHandler;
import net.sunken.master.network.handler.PlayerProxyQuitHandler;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class NetworkManager implements Facet, Enableable {

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private PlayerProxyJoinHandler playerProxyJoinHandler;
    @Inject
    private PlayerProxyQuitHandler playerProxyQuitHandler;

    @Getter
    private Set<PlayerDetail> onlinePlayers;

    public NetworkManager() {
        this.onlinePlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public void add(@NonNull PlayerDetail abstractPlayer) {
        onlinePlayers.add(abstractPlayer);
    }

    public Optional<PlayerDetail> get(@NonNull UUID uuid) {
        return onlinePlayers.stream()
                .filter(playerDetail -> playerDetail.getUuid().equals(uuid))
                .findFirst();
    }

    public Optional<PlayerDetail> get(@NonNull String username) {
        return onlinePlayers.stream()
                .filter(playerDetail -> playerDetail.getDisplayName().equalsIgnoreCase(username))
                .findFirst();
    }

    public void remove(@NonNull UUID uuid) {
        onlinePlayers.removeIf(playerDetail -> playerDetail.getUuid().equals(uuid));
    }

    @Override
    public void enable() {
        packetHandlerRegistry.registerHandler(PlayerProxyJoinPacket.class, playerProxyJoinHandler);
        packetHandlerRegistry.registerHandler(PlayerProxyQuitPacket.class, playerProxyQuitHandler);
    }

    @Override
    public void disable() {
    }

}
