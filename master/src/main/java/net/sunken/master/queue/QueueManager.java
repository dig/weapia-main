package net.sunken.master.queue;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.inject.Disableable;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.player.packet.PlayerSaveStatePacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.master.queue.handler.PlayerRequestServerHandler;
import net.sunken.master.queue.handler.PlayerSaveStateHandler;
import net.sunken.master.queue.impl.AbstractBalancer;
import net.sunken.master.queue.impl.BalancerFactory;
import net.sunken.master.queue.impl.LobbyBalancer;
import net.sunken.master.queue.impl.SimpleBalancer;

import java.util.Map;
import java.util.UUID;

@Log
@Singleton
public class QueueManager implements Facet, Enableable, Disableable {

    @Inject
    private QueueThread queueThread;
    @Inject
    private BalancerFactory balancerFactory;

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private PlayerRequestServerHandler playerRequestServerHandler;
    @Inject
    private PlayerSaveStateHandler playerSaveStateHandler;

    @Getter
    private final Map<Game, AbstractBalancer> gameBalancers = Maps.newHashMap();
    @Getter
    private LobbyBalancer lobbyBalancer;

    @Override
    public void enable() {
        lobbyBalancer = (LobbyBalancer) balancerFactory.create(LobbyBalancer.class);
        gameBalancers.put(Game.ICE_RUNNER_SOLO, balancerFactory.create(SimpleBalancer.class));

        gameBalancers.put(Game.SURVIVAL_REALMS, balancerFactory.create(LobbyBalancer.class));
        gameBalancers.put(Game.SURVIVAL_REALMS_ADVENTURE, balancerFactory.create(LobbyBalancer.class));

        packetHandlerRegistry.registerHandler(PlayerRequestServerPacket.class, playerRequestServerHandler);
        packetHandlerRegistry.registerHandler(PlayerSaveStatePacket.class, playerSaveStateHandler);

        queueThread.start();
    }

    @Override
    public void disable() {
        queueThread.interrupt();
    }

    public boolean queue(@NonNull UUID uuid, @NonNull Server.Type type, @NonNull Game game) {
        if ((gameBalancers.containsKey(game) || type == Server.Type.LOBBY) && !inQueue(uuid)) {
            AbstractBalancer balancer = type == Server.Type.LOBBY ? lobbyBalancer : gameBalancers.get(game);
            return balancer.add(new QueueDetail(uuid, type, game));
        }

        return false;
    }

    public boolean inQueue(@NonNull UUID uuid) {
        boolean queued = false;
        for (AbstractBalancer balancer : gameBalancers.values()) {
            if (balancer.inQueue(uuid)) {
                queued = true;
            }
        }

        return queued;
    }

    private static class QueueThread extends Thread {

        @Inject
        private QueueManager queueManager;

        public void run() {
            while (true) {
                queueManager.getLobbyBalancer().run();
                queueManager.getGameBalancers().values().forEach(AbstractBalancer::run);
            }
        }
    }
}
