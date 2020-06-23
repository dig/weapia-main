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
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private PlayerRequestServerHandler playerRequestServerHandler;
    @Inject
    private PlayerSaveStateHandler playerSaveStateHandler;

    @Inject
    private BalancerFactory balancerFactory;
    @Getter
    private final Map<Game, AbstractBalancer> balancers = Maps.newHashMap();
    @Getter
    private AbstractBalancer lobbyBalancer;

    private QueueConsumer consumer;

    @Override
    public void enable() {
        lobbyBalancer = balancerFactory.create(LobbyBalancer.class);
        balancers.put(Game.ICE_RUNNER_SOLO, balancerFactory.create(SimpleBalancer.class));

        balancers.put(Game.SURVIVAL_REALMS, balancerFactory.create(LobbyBalancer.class));
        balancers.put(Game.SURVIVAL_REALMS_ADVENTURE, balancerFactory.create(LobbyBalancer.class));

        // start consumer
        consumer = new QueueConsumer(balancers, lobbyBalancer);
        consumer.start();

        log.info("consumer started");

        packetHandlerRegistry.registerHandler(PlayerRequestServerPacket.class, playerRequestServerHandler);
        packetHandlerRegistry.registerHandler(PlayerSaveStatePacket.class, playerSaveStateHandler);
    }

    @Override
    public void disable() {
        consumer.interrupt();
    }

    public boolean queue(@NonNull UUID uuid, @NonNull Server.Type type, @NonNull Game game) {
        if ((balancers.containsKey(game) || type == Server.Type.LOBBY) && !inQueue(uuid)) {
            AbstractBalancer balancer = type == Server.Type.LOBBY ? lobbyBalancer : balancers.get(game);
            return balancer.add(new QueueDetail(uuid, type, game));
        }
        return false;
    }

    public boolean inQueue(@NonNull UUID uuid) {
        boolean queued = false;
        for (AbstractBalancer balancer : balancers.values()) {
            if (balancer.inQueue(uuid)) {
                queued = true;
            }
        }
        return queued;
    }
}
