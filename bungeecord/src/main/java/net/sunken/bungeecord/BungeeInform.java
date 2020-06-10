package net.sunken.bungeecord;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerInformer;
import net.sunken.common.server.World;
import net.sunken.common.util.AsyncHelper;

import java.util.HashMap;

@Singleton
public class BungeeInform implements Facet, Enableable, Listener {

    @Inject
    private ServerInformer serverInformer;
    @Inject
    private ProxyServer proxyServer;

    @Getter
    private Server server;

    @Override
    public void enable() {
        server = Server.builder()
                .id(Server.Type.BUNGEE.generateId())
                .type(Server.Type.BUNGEE)
                .host("0.0.0.0")
                .port(25565)
                .game(Game.NONE)
                .world(World.NONE)
                .players(proxyServer.getOnlineCount())
                .maxPlayers(500)
                .state(Server.State.OPEN)
                .metadata(new HashMap<>())
                .build();

        serverInformer.add(server, true);
    }

    @Override
    public void disable() {
    }

    @EventHandler
    public void onPlayerLogin(PostLoginEvent event) {
        server.setPlayers(proxyServer.getOnlineCount());
        AsyncHelper.executor().submit(() -> serverInformer.update(server, true));
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        server.setPlayers(proxyServer.getOnlineCount() - 1);
        AsyncHelper.executor().submit(() -> serverInformer.update(server, true));
    }

    public void remove() {
        serverInformer.remove(server.getId(), true);
    }

}
