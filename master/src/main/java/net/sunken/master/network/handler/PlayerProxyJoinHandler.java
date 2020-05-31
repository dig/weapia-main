package net.sunken.master.network.handler;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.player.packet.PlayerProxyJoinPacket;
import net.sunken.master.network.NetworkManager;

@Log
public class PlayerProxyJoinHandler extends PacketHandler<PlayerProxyJoinPacket> {

    @Inject
    private NetworkManager networkManager;

    @Override
    public void onReceive(PlayerProxyJoinPacket packet) {
        networkManager.add(packet.getPlayer());
        log.info(String.format("Adding player to network. (%s, %s)", packet.getPlayer().getUuid(), packet.getPlayer().getDisplayName()));
    }

}
