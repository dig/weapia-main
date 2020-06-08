package net.sunken.master.instance;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.java.Log;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.server.*;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.server.packet.RequestServerCreationPacket;
import net.sunken.common.util.AsyncHelper;
import net.sunken.common.util.DummyObject;
import net.sunken.master.instance.creation.RequestServerCreationHandler;
import net.sunken.master.kube.Kube;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@Log
@Singleton
public class InstanceManager implements Facet, Enableable {

    @Getter
    private Queue<InstanceDetail> pendingInstanceCreation;
    @Getter
    private Cache<Server, DummyObject> pendingInstanceStart;

    @Inject
    private ServerManager serverManager;
    @Inject
    private InstanceRunnable instanceRunnable;
    @Inject
    private ServerInformer serverInformer;
    @Inject
    private Kube kubeApi;

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private RequestServerCreationHandler requestServerCreationHandler;

    @Override
    public void enable() {
        pendingInstanceCreation = new ConcurrentLinkedQueue<>();
        pendingInstanceStart = CacheBuilder.newBuilder()
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .build();

        packetHandlerRegistry.registerHandler(RequestServerCreationPacket.class, requestServerCreationHandler);
        AsyncHelper.scheduledExecutor().scheduleAtFixedRate(instanceRunnable, 200L, 200L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void disable() {
    }

    public long findPendingCount(Server.Type type, Game game) {
        long pendingCreation = pendingInstanceCreation.stream()
                .filter(instanceDetail -> instanceDetail.getType() == type && instanceDetail.getGame() == game)
                .count();

        long pendingStart = pendingInstanceStart.asMap().keySet().stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .count();

        return pendingCreation + pendingStart;
    }

    public long findPendingStartCount(Server.Type type, Game game) {
        long pendingStart = pendingInstanceStart.asMap().keySet().stream()
                .filter(server -> server.getType() == type && server.getGame() == game)
                .count();

        return pendingStart;
    }

    public void createInstance(Server.Type type, Game game, int amount, Reason reason) {
        for (int i = 0; i < amount; i++)
            pendingInstanceCreation.add(new InstanceDetail(type, game));
    }

    public void createInstance(Server.Type type, Game game, Reason reason) {
        createInstance(type, game, 1, reason);
    }

    public void removeInstance(Server server, Reason reason) {
        kubeApi.deletePod(server.getId());
        serverManager.getServerList().removeIf(srv -> srv.getId().equals(server.getId()));
        serverInformer.remove(server.getId(), true);
    }

    private static class InstanceRunnable implements Runnable {

        @Inject
        private InstanceManager instanceManager;
        @Inject
        private ServerManager serverManager;
        @Inject
        private Kube kubeApi;

        @Override
        public void run() {
            Queue<InstanceDetail> pendingInstanceCreation = instanceManager.getPendingInstanceCreation();
            while (!pendingInstanceCreation.isEmpty()) {
                InstanceDetail instanceDetail = pendingInstanceCreation.poll();

                Server.Type type = instanceDetail.getType();
                Game game = instanceDetail.getGame();

                Map<String, String> metadata = new HashMap<>();
                if (type.isAssignId()) {
                    metadata.put(ServerHelper.SERVER_METADATA_ID_KEY, String.valueOf(serverManager.findNextAvailableID(type, game)));
                }

                Server instanceCreate = Server.builder()
                        .id(type.generateId())
                        .type(type)
                        .host(null)
                        .port(25565)
                        .game(game)
                        .world(type == Server.Type.INSTANCE ? World.getRandomWorld(game) : World.LOBBY)
                        .players(0)
                        .maxPlayers(game.getMaxPlayers())
                        .state(Server.State.PENDING)
                        .metadata(metadata)
                        .build();

                if (kubeApi.createPod(instanceCreate)) {
                    instanceManager.getPendingInstanceStart().put(instanceCreate, new DummyObject());
                }
            }
        }

    }

    // TODO: priority queue for some creation reasons
    public enum Reason {
        QUEUE,
        HEARTBEAT,
        COMMAND
    }

}
