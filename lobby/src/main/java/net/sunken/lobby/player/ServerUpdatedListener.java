package net.sunken.lobby.player;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.event.ListensToEvent;
import net.sunken.common.event.SunkenListener;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.PlayerManager;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.server.module.event.ServerUpdatedEvent;
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import org.bukkit.ChatColor;

@Log
public class ServerUpdatedListener implements Facet, SunkenListener {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private ServerManager serverManager;
    @Inject
    private BukkitSyncExecutor bukkitSyncExecutor;
    @Inject
    private ScoreboardRegistry scoreboardRegistry;

    @ListensToEvent
    public void onServerUpdated(ServerUpdatedEvent event) {
        if (event.getServer().getType() == Server.Type.BUNGEE) {
            bukkitSyncExecutor.execute(() ->
                playerManager.getOnlinePlayers().stream()
                        .filter(abstractPlayer -> scoreboardRegistry.get(abstractPlayer.getUuid().toString()).isPresent())
                        .map(abstractPlayer -> scoreboardRegistry.get(abstractPlayer.getUuid().toString()).get())
                        .forEach(scoreboard -> scoreboard.getEntry("PlayersValue").update(ChatColor.YELLOW + "" + serverManager.getTotalPlayersOnline()))
            );
        }
    }
}
