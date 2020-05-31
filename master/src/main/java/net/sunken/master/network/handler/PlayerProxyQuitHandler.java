package net.sunken.master.network.handler;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.player.packet.PlayerProxyQuitPacket;
import net.sunken.master.network.NetworkManager;

@Log
public class PlayerProxyQuitHandler extends PacketHandler<PlayerProxyQuitPacket> {

    @Inject
    private NetworkManager networkManager;

    @Override
    public void onReceive(PlayerProxyQuitPacket packet) {
        networkManager.remove(packet.getUuid());
        log.info(String.format("Removing player from network. (%s)", packet.getUuid().toString()));
    }

}
