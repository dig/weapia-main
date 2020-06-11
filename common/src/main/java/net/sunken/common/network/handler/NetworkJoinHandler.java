package net.sunken.common.network.handler;

import com.google.inject.Inject;
import net.sunken.common.network.NetworkManager;
import net.sunken.common.network.packet.NetworkJoinPacket;
import net.sunken.common.packet.PacketHandler;

public class NetworkJoinHandler extends PacketHandler<NetworkJoinPacket> {

    @Inject
    private NetworkManager networkManager;

    @Override
    public void onReceive(NetworkJoinPacket packet) {
        networkManager.add(packet.getPlayer(), true);
    }

}
