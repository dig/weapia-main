package net.sunken.master.queue.handler;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.network.packet.NetworkQuitPacket;
import net.sunken.common.packet.PacketHandler;
import net.sunken.master.queue.QueueManager;

@Log
public class NetworkQuitHandler extends PacketHandler<NetworkQuitPacket> {

    @Inject
    private QueueManager queueManager;

    @Override
    public void onReceive(NetworkQuitPacket packet) {
        queueManager.removeIfPresent(packet.getPlayer().getUuid());
    }

}
