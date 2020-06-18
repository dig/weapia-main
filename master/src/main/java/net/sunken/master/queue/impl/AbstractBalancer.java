package net.sunken.master.queue.impl;

import com.google.common.collect.Queues;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerSendToServerPacket;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerDetail;
import net.sunken.common.server.module.ServerManager;
import net.sunken.master.party.Party;
import net.sunken.master.party.PartyManager;
import net.sunken.master.queue.QueueDetail;

import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

@Log
public abstract class AbstractBalancer {

    protected final PartyManager partyManager;
    protected final ServerManager serverManager;
    protected final PacketUtil packetUtil;

    protected final Queue<QueueDetail> queue = Queues.newConcurrentLinkedQueue();

    public AbstractBalancer(PartyManager partyManager, ServerManager serverManager, PacketUtil packetUtil) {
        this.partyManager = partyManager;
        this.serverManager = serverManager;
        this.packetUtil = packetUtil;
    }

    public boolean add(@NonNull QueueDetail queueDetail) {
        Optional<Party> partyOptional = partyManager.findPartyByMember(queueDetail.getUuid());
        if (partyOptional.isPresent()) {
            Party party = partyOptional.get();

            if (queueDetail.getUuid().equals(party.getLeaderUUID())) {
                queue.add(queueDetail);
                return true;
            }

            return false;
        }

        queue.add(queueDetail);
        return true;
    }

    public boolean inQueue(@NonNull UUID uuid) {
        Optional<QueueDetail> queueDetailOptional = queue.stream()
                .filter(queueDetail -> queueDetail.getUuid().equals(uuid))
                .findFirst();
        return queueDetailOptional.isPresent();
    }

    public void run() {
        while (!queue.isEmpty()) {
            QueueDetail queueDetail = queue.peek();
            if (handle(queueDetail.getUuid(), serverManager.findAllAvailable(queueDetail.getType(), queueDetail.getGame()))) {
                queue.poll();
            } else {
                log.info("Break out of balancer due to no available servers");
                break;
            }
        }
    }

    protected boolean handle(@NonNull UUID uuid, @NonNull Set<Server> servers) {
        Optional<Party> partyOptional = partyManager.findPartyByMember(uuid);

        int amountNeeded = 1;
        if (partyOptional.isPresent()) {
            Party party = partyOptional.get();
            amountNeeded = party.getMembers().size();
        }

        int finalAmountNeeded = amountNeeded;
        Optional<Server> serverOptional = servers.stream()
                .filter(Server::canJoin)
                .filter(server -> ((server.getPlayers() + (int) serverManager.findPendingConnectionCount(server)) + finalAmountNeeded) <= server.getMaxPlayers())
                .findFirst();

        if (serverOptional.isPresent()) {
            Server server = serverOptional.get();
            ServerDetail serverDetail = server.toServerDetail();

            if (partyOptional.isPresent()) {
                Party party = partyOptional.get();
                party.getMembers().forEach(playerDetail -> packetUtil.sendSync(new PlayerSendToServerPacket(playerDetail.getUuid(), serverDetail)));
            } else {
                packetUtil.sendSync(new PlayerSendToServerPacket(uuid, serverDetail));
            }

            log.info(String.format("Sending player to instance. (%s, %s)", uuid, server.getId()));
            return true;
        }

        return false;
    }

}
