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
        serverManager.findServerById(packet.getServerId())
                .filter(server -> server.getType() != Server.Type.BUNGEE)
                .ifPresent(server -> serverManager.getPendingPlayerConnection().invalidate(packet.getUuid()));
    }
}
