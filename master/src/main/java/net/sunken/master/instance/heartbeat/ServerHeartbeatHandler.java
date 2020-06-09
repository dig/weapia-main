package net.sunken.master.instance.heartbeat;

import com.google.inject.Inject;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.server.packet.ServerHeartbeatPacket;

public class ServerHeartbeatHandler extends PacketHandler<ServerHeartbeatPacket> {

    @Inject
    private HeartbeatManager heartbeatManager;

    @Override
    public void onReceive(ServerHeartbeatPacket packet) {
        if (packet.getReason() == ServerHeartbeatPacket.Reason.RESPOND) {
            heartbeatManager.responded(packet.getServerId());
        }
    }

}
