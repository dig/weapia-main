package net.sunken.master.queue.impl.balancer;

import com.google.common.collect.Queues;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.PlayerDetail;
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
        PlayerDetail instigator = queueDetail.getInstigator();

        Optional<Party> partyOptional = partyManager.findPartyByMember(instigator.getUuid());
        if (partyOptional.isPresent()) {
            Party party = partyOptional.get();

            if (instigator.getUuid().equals(party.getLeaderUUID())) {
                queue.add(queueDetail);
                return true;
            }

            return false;
        }

        queue.add(queueDetail);
        return true;
    }

    public void run() {
        while (!queue.isEmpty()) {
            QueueDetail queueDetail = queue.peek();
            if (handle(queueDetail.getInstigator(), serverManager.findAllAvailable(queueDetail.getType(), queueDetail.getGame()))) {
                queue.poll();
            } else {
                log.info("Break out of balancer due to no available servers");
                break;
            }
        }
    }

    private boolean handle(@NonNull PlayerDetail instigator, @NonNull Set<Server> servers) {
        Optional<Party> partyOptional = partyManager.findPartyByMember(instigator.getUuid());

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
                packetUtil.sendSync(new PlayerSendToServerPacket(instigator.getUuid(), serverDetail));
            }

            log.info(String.format("Sending player to instance. (%s, %s)", instigator.getUuid(), server.getId()));
            return true;
        }

        return false;
    }

}
