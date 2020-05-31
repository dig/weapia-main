package net.sunken.common.server.module;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.server.Server;
import net.sunken.common.server.packet.ServerConnectedPacket;

import java.util.Optional;

@Log
public class ServerConnectedHandler extends PacketHandler<ServerConnectedPacket> {

    @Inject
    private ServerManager serverManager;

    @Override
    public void onReceive(ServerConnectedPacket packet) {
        Optional<Server> connectedToServer = serverManager.findServerById(packet.getServerId());

        if (connectedToServer.isPresent()) {
            Server server = connectedToServer.get();

            if (server.getType() != Server.Type.BUNGEE) {
                serverManager.getPendingPlayerConnection().invalidate(packet.getUuid());
            }

            log.info(String.format("Player connected to server. (%s, %s)", packet.getUuid().toString(), packet.getServerId()));
        } else {
           log.severe(String.format("Player connected to a server which isn't stored locally? huh"));
        }
    }

}
