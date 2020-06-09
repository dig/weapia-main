package net.sunken.master.instance;

import com.google.common.collect.Queues;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.server.*;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.server.packet.RequestServerCreationPacket;
import net.sunken.common.util.AsyncHelper;
import net.sunken.master.instance.creation.RequestServerCreationHandler;
import net.sunken.master.kube.Kube;
import net.sunken.master.kube.KubeConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Log
@Singleton
public class InstanceManager implements Facet, Enableable {

    @Getter
    private Queue<InstanceDetail> pendingInstanceCreation;

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

    @Inject @InjectConfig
    private KubeConfiguration kubeConfiguration;

    @Override
    public void enable() {
        pendingInstanceCreation = Queues.newConcurrentLinkedQueue();
        packetHandlerRegistry.registerHandler(RequestServerCreationPacket.class, requestServerCreationHandler);

        if (kubeConfiguration.isKubernetes()) {
            AsyncHelper.scheduledExecutor().scheduleAtFixedRate(instanceRunnable, 200L, 200L, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void disable() {
    }

    public long findPendingCount(@NonNull Server.Type type, @NonNull Game game) {
        long pendingCreation = pendingInstanceCreation.stream()
                .filter(instanceDetail -> instanceDetail.getType() == type && instanceDetail.getGame() == game)
                .count();

        return pendingCreation;
    }

    public void createInstance(@NonNull Server.Type type, @NonNull Game game, int amount, @NonNull Reason reason) {
        for (int i = 0; i < amount; i++) {
            pendingInstanceCreation.add(new InstanceDetail(type, game));
        }
    }

    public void createInstance(@NonNull Server.Type type, @NonNull Game game, @NonNull Reason reason) {
        createInstance(type, game, 1, reason);
    }

    public void removeInstance(@NonNull Server server, @NonNull Reason reason) {
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

                Server server = Server.builder()
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

                if (kubeApi.createPod(server)) {
                    serverManager.getServerList().add(server);
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
