package net.sunken.master.queue;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.PlayerDetail;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.ServerManager;
import net.sunken.master.party.PartyManager;
import net.sunken.master.queue.impl.balancer.AbstractBalancer;
import net.sunken.master.queue.impl.balancer.LobbyBalancer;
import net.sunken.master.queue.impl.balancer.SimpleBalancer;

import java.util.Map;

@Log
@Singleton
public class QueueManager implements Facet, Enableable {

    @Inject
    private QueueThread queueThread;
    @Inject
    private PartyManager partyManager;
    @Inject
    private ServerManager serverManager;
    @Inject
    private PacketUtil packetUtil;

    @Getter
    private final Map<Game, AbstractBalancer> gameBalancers = Maps.newHashMap();
    @Getter
    private LobbyBalancer lobbyBalancer;

    @Override
    public void enable() {
        lobbyBalancer = new LobbyBalancer(partyManager, serverManager, packetUtil);
        gameBalancers.put(Game.ICE_RUNNER_SOLO, new SimpleBalancer(partyManager, serverManager, packetUtil));

        queueThread.start();
    }

    @Override
    public void disable() {
        queueThread.interrupt();
    }

    public boolean queue(@NonNull PlayerDetail instigator, @NonNull Server.Type type, @NonNull Game game) {
        if (gameBalancers.containsKey(game) || type == Server.Type.LOBBY) {
            AbstractBalancer balancer = type == Server.Type.LOBBY ? lobbyBalancer : gameBalancers.get(game);
            if (balancer.add(new QueueDetail(instigator, type, game))) {
                return true;
            }
        }

        return false;
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
