package net.sunken.common.network.handler;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.network.NetworkManager;
import net.sunken.common.network.packet.NetworkQuitPacket;
import net.sunken.common.packet.PacketHandler;

public class NetworkQuitHandler extends PacketHandler<NetworkQuitPacket> {

    @Inject
    private NetworkManager networkManager;

    @Override
    public void onReceive(NetworkQuitPacket packet) {
        networkManager.remove(packet.getPlayer(), true);
    }
}
