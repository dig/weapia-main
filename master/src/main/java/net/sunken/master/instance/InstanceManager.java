package net.sunken.master.instance;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.server.*;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.server.packet.RequestServerCreationPacket;
import net.sunken.master.instance.config.InstanceConfiguration;
import net.sunken.master.instance.config.InstanceGameConfiguration;
import net.sunken.master.instance.handler.RequestServerCreationHandler;
import net.sunken.master.kube.Kube;
import net.sunken.master.kube.KubeConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Log
@Singleton
public class InstanceManager implements Facet, Enableable {

    @Inject
    private ServerManager serverManager;
    @Inject
    private InstancePoolThread instancePoolThread;
    @Inject
    private Kube kubeApi;

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private RequestServerCreationHandler requestServerCreationHandler;

    @Inject @InjectConfig
    private KubeConfiguration kubeConfiguration;
    @Inject @InjectConfig
    private InstanceConfiguration instanceConfiguration;

    private Random random = new Random();

    @Override
    public void enable() {
        packetHandlerRegistry.registerHandler(RequestServerCreationPacket.class, requestServerCreationHandler);
        if (kubeConfiguration.isKubernetes()) {
            instancePoolThread.start();
        }
    }

    @Override
    public void disable() {
        instancePoolThread.interrupt();
    }

    public boolean create(@NonNull Server.Type type, @NonNull Game game) {
        if (kubeConfiguration.isKubernetes()) {
            Map<String, String> metadata = Maps.newHashMap();
            if (type.isAssignId()) {
                metadata.put(ServerHelper.SERVER_METADATA_ID_KEY, String.valueOf(serverManager.findNextAvailableID(type, game)));
            }

            World world = World.NONE;
            if (instanceConfiguration.getGames().containsKey(game)) {
                InstanceGameConfiguration instance = instanceConfiguration.getGames().get(game);
                List<World> worlds = instance.getWorlds();

                if (worlds != null && worlds.size() > 0) {
                    world = worlds.get(random.nextInt(worlds.size()));
                }
            }

            Server server = Server.builder()
                    .id(ServerHelper.generate(type))
                    .type(type)
                    .host(null)
                    .port(25565)
                    .game(game)
                    .world(type == Server.Type.INSTANCE ? world : World.LOBBY)
                    .players(0)
                    .maxPlayers(game.getMaxPlayers())
                    .state(Server.State.PENDING)
                    .metadata(metadata)
                    .build();

            if (kubeApi.createPod(server)) {
                serverManager.add(server, true);
                return true;
            }
        }

        return false;
    }

    public int create(@NonNull Server.Type type, @NonNull Game game, int amount) {
        int created = 0;
        for (int i = 0; i < amount; i++) {
            if (create(type, game)) {
                created++;
            }
        }
        return created;
    }

    public void remove(@NonNull String id) {
        serverManager.remove(id, false);
        if (kubeConfiguration.isKubernetes()) {
            kubeApi.deletePod(id);
        }
    }

    @Log
    private static class InstancePoolThread extends Thread {

        @Inject
        private InstanceManager instanceManager;
        @Inject
        private ServerManager serverManager;
        @Inject @InjectConfig
        private InstanceConfiguration instanceConfiguration;

        public void run() {
            while (true) {
                for (Server.Type type : instanceConfiguration.getTypes().keySet()) {
                    InstanceGameConfiguration config = instanceConfiguration.getTypes().get(type);
                    long count = serverManager.findAllAvailableCount(type, Game.NONE) + serverManager.findAllPendingCount(type, Game.NONE);

                    if (count < config.getPool().getMin()) {
                        int amountToMeetMin = config.getPool().getMin() - (int) count;
                        instanceManager.create(type, Game.NONE, amountToMeetMin);
                        log.info(String.format("Starting instances (%s, %s)", type, amountToMeetMin));
                    }
                }

                for (Game game : instanceConfiguration.getGames().keySet()) {
                    InstanceGameConfiguration config = instanceConfiguration.getGames().get(game);
                    long count = serverManager.findAllAvailableCount(Server.Type.INSTANCE, game) + serverManager.findAllPendingCount(Server.Type.INSTANCE, game);

                    if (count < config.getPool().getMin()) {
                        int amountToMeetMin = config.getPool().getMin() - (int) count;
                        instanceManager.create(Server.Type.INSTANCE, game, amountToMeetMin);
                        log.info(String.format("Starting instances (%s, %s)", game, amountToMeetMin));
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
