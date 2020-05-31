package net.sunken.master.queue.handler;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.server.packet.ServerDisconnectedPacket;
import net.sunken.master.queue.QueueManager;

import java.util.Optional;

@Log
public class ServerDisconnectedHandler extends PacketHandler<ServerDisconnectedPacket> {

    @Inject
    private ServerManager serverManager;
    @Inject
    private QueueManager queueManager;

    @Override
    public void onReceive(ServerDisconnectedPacket packet) {
        Optional<Server> disconnectedFromServer = serverManager.findServerById(packet.getServerId());

        if (disconnectedFromServer.isPresent()) {
            Server server = disconnectedFromServer.get();

            switch (server.getType()) {
                case BUNGEE:
                    queueManager.removeIfPresent(packet.getUuid());
                    break;
            }

            log.info(String.format("Player disconnected from server. (%s, %s)", packet.getUuid().toString(), packet.getServerId()));
        } else {
            log.severe(String.format("Player disconnected from a server which isn't stored locally? huh"));
        }
    }

}
