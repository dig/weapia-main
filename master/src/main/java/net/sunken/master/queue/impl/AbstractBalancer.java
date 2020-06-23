package net.sunken.master.queue.impl;

import com.google.common.collect.Queues;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerSendToServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerDetail;
import net.sunken.common.server.module.ServerManager;
import net.sunken.master.instance.InstanceManager;
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
    protected final InstanceManager instanceManager;
    protected final PacketUtil packetUtil;

    protected final Queue<QueueDetail> queue = Queues.newArrayDeque();

    public AbstractBalancer(PartyManager partyManager, ServerManager serverManager, InstanceManager instanceManager, PacketUtil packetUtil) {
        this.partyManager = partyManager;
        this.serverManager = serverManager;
        this.instanceManager = instanceManager;
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
                handleInstanceCreation(queueDetail.getType(), queueDetail.getGame());
                break;
            }
        }
    }

    protected void handleInstanceCreation(@NonNull Server.Type type, @NonNull Game game) {
        Set<Server> availableInstances = serverManager.findAllAvailable(type, game);

        long pendingInstancesCount = serverManager.findAll().stream()
                .filter(server -> server.getType() == type && server.getGame() == game && server.getState() == Server.State.PENDING)
                .count();

        long availableInstanceSlots = 0;
        for (Server server : availableInstances) {
            availableInstanceSlots += (server.getMaxPlayers() - (server.getPlayers() + serverManager.findPendingConnectionCount(server)));
        }

        long totalAmountOfSlotsAvailable = (availableInstanceSlots + (pendingInstancesCount * game.getMaxPlayers()));
        int amountOfQueuedPlayers = queue.size();

        if (totalAmountOfSlotsAvailable < amountOfQueuedPlayers) {
            long amountOfInstancesNeeded = (long) Math.ceil(((double) amountOfQueuedPlayers - (double) totalAmountOfSlotsAvailable) / (double) game.getMaxPlayers());
            int created = instanceManager.create(type, game, (int) amountOfInstancesNeeded);
            log.info(String.format("Created %d of %s %s", created, type.toString(), game.toString()));
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
                party.getMembers().forEach(playerDetail -> packetUtil.send(new PlayerSendToServerPacket(playerDetail.getUuid(), serverDetail)));
            } else {
                packetUtil.send(new PlayerSendToServerPacket(uuid, serverDetail));
                log.info(String.format("Sending %s to %s.", uuid, serverDetail));
            }
            return true;
        }

        return false;
    }
}
