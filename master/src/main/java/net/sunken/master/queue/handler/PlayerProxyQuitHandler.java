package net.sunken.master.queue.handler;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.player.packet.PlayerProxyQuitPacket;
import net.sunken.master.queue.QueueManager;

@Log
public class PlayerProxyQuitHandler extends PacketHandler<PlayerProxyQuitPacket> {

    @Inject
    private QueueManager queueManager;

    @Override
    public void onReceive(PlayerProxyQuitPacket packet) {
        queueManager.removeIfPresent(packet.getUuid());
    }

}
