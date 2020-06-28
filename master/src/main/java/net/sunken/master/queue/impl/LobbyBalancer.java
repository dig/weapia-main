package net.sunken.master.queue.impl;

import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerSendToServerPacket;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerDetail;
import net.sunken.common.server.module.ServerManager;
import net.sunken.master.instance.InstanceManager;
import net.sunken.master.party.PartyManager;
import net.sunken.master.queue.QueueDetail;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Log
public class LobbyBalancer extends AbstractBalancer {

    public LobbyBalancer(PartyManager partyManager, ServerManager serverManager, InstanceManager instanceManager, PacketUtil packetUtil) {
        super(partyManager, serverManager, instanceManager, packetUtil);
    }

    @Override
    public boolean add(@NonNull QueueDetail queueDetail) {
        queue.add(queueDetail);
        log.info("Adding to lobby queue " + queueDetail.getUuid().toString());
        return true;
    }

    @Override
    protected boolean handle(@NonNull UUID uuid, @NonNull Set<Server> servers) {
        Optional<Server> serverOptional = servers.stream()
                .filter(Server::canJoin)
                .filter(server -> (server.getPlayers() + (int) serverManager.findPendingConnectionCount(server)) < server.getMaxPlayers())
                .findFirst();

        if (serverOptional.isPresent()) {
            Server server = serverOptional.get();
            ServerDetail serverDetail = server.toServerDetail();
            packetUtil.send(new PlayerSendToServerPacket(uuid, serverDetail));
            log.info(String.format("Sending %s to %s.", uuid, serverDetail));

            return true;
        }

        return false;
    }
}
