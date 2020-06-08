package net.sunken.common.server.module;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.event.EventManager;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.event.ServerRemovedEvent;
import net.sunken.common.server.packet.ServerRemovePacket;

import java.util.Optional;

@Log
public class ServerRemoveHandler extends PacketHandler<ServerRemovePacket> {

    @Inject
    private ServerManager serverManager;
    @Inject
    private EventManager eventManager;

    @Override
    public void onReceive(ServerRemovePacket packet) {
        Optional<Server> serverOptional = serverManager.findServerById(packet.getId());
        serverManager.getServerList().removeIf(server -> server.getId().equals(packet.getId()));

        if (serverOptional.isPresent()) {
            eventManager.callEvent(new ServerRemovedEvent(serverOptional.get()));
        }

        log.info(String.format("ServerRemovePacket (%s)", packet.getId()));
    }

}
