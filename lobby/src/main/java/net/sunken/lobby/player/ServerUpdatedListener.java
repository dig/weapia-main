package net.sunken.lobby.player;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.event.ListensToEvent;
import net.sunken.common.event.SunkenListener;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.event.ServerUpdatedEvent;
import net.sunken.core.executor.BukkitSyncExecutor;

@Log
public class ServerUpdatedListener implements Facet, SunkenListener {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private BukkitSyncExecutor bukkitSyncExecutor;

    @ListensToEvent
    public void onServerUpdated(ServerUpdatedEvent event) {
        if (event.getServer().getType() == Server.Type.BUNGEE)
            bukkitSyncExecutor.execute(() -> playerManager.getOnlinePlayers().forEach(abstractPlayer -> ((LobbyPlayer) abstractPlayer).onServerUpdate()));
    }

}
