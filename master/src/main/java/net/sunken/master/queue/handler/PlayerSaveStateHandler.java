package net.sunken.master.queue.handler;

import com.google.inject.Inject;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.player.packet.PlayerSaveStatePacket;

public class PlayerSaveStateHandler extends PacketHandler<PlayerSaveStatePacket> {

    @Inject
    private PlayerRequestServerHandler playerRequestServerHandler;

    @Override
    public void onReceive(PlayerSaveStatePacket packet) {
        if (packet.getReason() != PlayerSaveStatePacket.Reason.REQUEST)
            playerRequestServerHandler.connected(packet.getUuid(), packet.getReason());
    }

}
