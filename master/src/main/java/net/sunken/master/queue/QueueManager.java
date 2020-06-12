package net.sunken.master.queue;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.java.Log;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.network.packet.NetworkQuitPacket;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.*;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerDetail;
import net.sunken.common.util.AsyncHelper;
import net.sunken.master.instance.InstanceDetail;
import net.sunken.master.instance.InstanceManager;
import net.sunken.master.party.Party;
import net.sunken.master.party.PartyManager;
import net.sunken.master.queue.handler.NetworkQuitHandler;
import net.sunken.master.queue.handler.PlayerRequestServerHandler;
import net.sunken.common.server.module.ServerManager;
import net.sunken.master.queue.handler.PlayerRequestServerIDHandler;
import net.sunken.master.queue.handler.PlayerSaveStateHandler;
import net.sunken.master.queue.impl.IQueue;
import net.sunken.master.queue.impl.PartyQueue;
import net.sunken.master.queue.impl.PlayerQueue;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Log
@Singleton
public class QueueManager implements Facet, Enableable {

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private PlayerRequestServerHandler playerRequestServerHandler;
    @Inject
    private PlayerSaveStateHandler playerSaveStateHandler;
    @Inject
    private NetworkQuitHandler playerProxyQuitHandler;
    @Inject
    private PlayerRequestServerIDHandler playerRequestServerIDHandler;

    @Inject
    private QueueRunnable queueRunnable;
    @Inject
    private ServerManager serverManager;
    @Inject
    private PartyManager partyManager;
    @Inject
    private PacketUtil packetUtil;

    @Getter
    private Queue<IQueue> lobbyQueue;
    @Getter
    private Map<Game, Queue<IQueue>> gameQueue;

    @Override
    public void enable() {
        lobbyQueue = Queues.newConcurrentLinkedQueue();
        gameQueue = Maps.newConcurrentMap();
        for (Game game : Game.values()) {
            gameQueue.put(game, Queues.newConcurrentLinkedQueue());
        }

        packetHandlerRegistry.registerHandler(NetworkQuitPacket.class, playerProxyQuitHandler);
        packetHandlerRegistry.registerHandler(PlayerRequestServerPacket.class, playerRequestServerHandler);
        packetHandlerRegistry.registerHandler(PlayerSaveStatePacket.class, playerSaveStateHandler);
        packetHandlerRegistry.registerHandler(PlayerRequestServerIDPacket.class, playerRequestServerIDHandler);

        AsyncHelper.scheduledExecutor().scheduleAtFixedRate(queueRunnable, 200L, 200L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void disable() {
    }

    public void queue(UUID uuid, Server.Type type, Game game) {
        Optional<Party> partyOptional = partyManager.findPartyByMember(uuid);
        if (partyOptional.isPresent() && type != Server.Type.LOBBY) {
            Party party = partyOptional.get();

            if (!party.getLeaderUUID().equals(uuid)) {
                packetUtil.send(new PlayerProxyMessagePacket(uuid, "&cYou must be the party leader to queue."));
                return;
            }
        }

        removeIfPresent(uuid);

        IQueue iQueue;
        if (!serverManager.hasPendingConnection(uuid)) {
            if (partyOptional.isPresent() && type != Server.Type.LOBBY) {
                iQueue = new PartyQueue(partyOptional.get());
            } else {
                iQueue = new PlayerQueue(uuid);
            }

            if (iQueue != null) {
                if (type == Server.Type.LOBBY) {
                    lobbyQueue.add(iQueue);
                } else {
                    (gameQueue.get(game)).add(iQueue);
                }
            }

            log.info(String.format("Added player to queue. (%s, %s, %s)", uuid.toString(), type.toString(), game.toString()));
        } else {
            log.info(String.format("Player tried to join queue while has pending connection. (%s)", uuid.toString()));
        }
    }

    public void queue(UUID uuid, InstanceDetail instanceDetail) {
        queue(uuid, instanceDetail.getType(), instanceDetail.getGame());
    }

    public boolean inQueue(UUID uuid, Server.Type type, Game game) {
        Queue<IQueue> foundQueue = (type == Server.Type.LOBBY ? lobbyQueue : gameQueue.get(game));

        Optional<IQueue> queueDetailOptional = foundQueue.stream()
                .filter(iQueue -> iQueue.getMembers().contains(uuid))
                .findFirst();

        return queueDetailOptional.isPresent();
    }

    public boolean inQueue(UUID uuid) {
        boolean inGameQueue = false;
        for (Game game : Game.values()) {
            if (!inGameQueue) inGameQueue = inQueue(uuid, Server.Type.INSTANCE, game);
        }

        return inQueue(uuid, Server.Type.LOBBY, Game.NONE) || inGameQueue;
    }

    public int getAmountInQueue(Server.Type type, Game game) {
        Queue<IQueue> queueDetails = (type == Server.Type.LOBBY ? lobbyQueue : gameQueue.get(game));

        int amount = 0;
        for (IQueue iQueue : queueDetails) {
            amount += iQueue.getMembers().size();
        }

        return amount;
    }

    public void removeIfPresent(UUID uuid) {
        lobbyQueue.removeIf(iQueue -> iQueue.getMembers().contains(uuid));
        for (Game game : Game.values()) {
            (gameQueue.get(game)).removeIf(iQueue -> iQueue.getMembers().contains(uuid));
        }
    }

    private static class QueueRunnable implements Runnable {

        @Inject
        private ServerManager serverManager;
        @Inject
        private QueueManager queueManager;
        @Inject
        private InstanceManager instanceManager;
        @Inject
        private PacketUtil packetUtil;

        @Override
        public void run() {
            handleQueue(Server.Type.LOBBY, Game.NONE);
            for (Game game : Game.values()) {
                handleQueue(Server.Type.INSTANCE, game);
            }
        }

        private void handleQueue(Server.Type type, Game game) {
            Queue<IQueue> queueDetails = (type == Server.Type.LOBBY ? queueManager.getLobbyQueue() : queueManager.getGameQueue().get(game));
            Set<Server> availableInstances = serverManager.findAllAvailable(type, game);

            //--- Pending instances, about to start of such type & game.
            long pendingInstancesCount = serverManager.findAll().stream()
                    .filter(server -> server.getType() == type && server.getGame() == game && server.getState() == Server.State.PENDING)
                    .count();
            pendingInstancesCount += instanceManager.findPendingCount(type, game);

            //--- Total amount of available slots for such type & game.
            long availableInstanceSlots = 0;
            for (Server server : availableInstances) {
                availableInstanceSlots += (server.getMaxPlayers() - (server.getPlayers() + serverManager.findPendingConnectionCount(server)));
            }

            //--- Check if we need to start more servers due to high demand in queue
            long totalAmountOfSlotsAvailable = (availableInstanceSlots + (pendingInstancesCount * game.getMaxPlayers()));
            int amountOfQueuedPlayers = queueManager.getAmountInQueue(type, game);

            if (totalAmountOfSlotsAvailable < amountOfQueuedPlayers) {
                long amountOfInstancesNeeded = (long) Math.ceil(((double) amountOfQueuedPlayers - (double) totalAmountOfSlotsAvailable) / (double) game.getMaxPlayers());

                instanceManager.createInstance(type, game, (int) amountOfInstancesNeeded, InstanceManager.Reason.QUEUE);

                //--- Debug, going to leave this here for a while.
                log.info(String.format("Starting instances. (%s, %s, %s)", type.toString(), game.toString(), amountOfInstancesNeeded));
                log.info(String.format("totalAmountOfSlotsAvailable = %s", totalAmountOfSlotsAvailable));
                log.info(String.format("availableInstanceSlots = %s", availableInstanceSlots));
                log.info(String.format("pendingInstancesCount = %s (%s slots)", pendingInstancesCount, pendingInstancesCount * game.getMaxPlayers()));
                log.info(String.format("amountOfQueuedPlayers = %s", amountOfQueuedPlayers));
            }

            //--- Send players to servers
            while (!queueDetails.isEmpty() && availableInstanceSlots > 0) {
                IQueue next = queueDetails.peek();
                Optional<Server> serverOptional = serverManager.findAvailable(type, game, next.getMembers().size());

                if (serverOptional.isPresent()) {
                    next = queueDetails.poll();

                    Server serverToJoin = serverOptional.get();
                    ServerDetail serverDetail = serverToJoin.toServerDetail();

                    for (UUID uuid : next.getMembers()) {
                        serverManager.getPendingPlayerConnection().put(uuid, serverDetail);
                        packetUtil.send(new PlayerSendToServerPacket(uuid, serverDetail));
                        log.info(String.format("Sending player to instance. (%s, %s)", uuid.toString(), serverToJoin.getId()));
                    }
                } else {
                    log.info("Break because no available instance found for IQueue.");
                    break;
                }
            }
        }

    }

}
