package net.sunken.lobby.player;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.event.ListensToEvent;
import net.sunken.common.event.SunkenListener;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.server.module.event.ServerUpdatedEvent;
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.scoreboard.CustomScoreboard;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import org.bukkit.ChatColor;

import java.util.Optional;

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
        if (event.getServer().getType() == Server.Type.BUNGEE)
            bukkitSyncExecutor.execute(() -> playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
                Optional<CustomScoreboard> customScoreboardOptional = scoreboardRegistry.get(abstractPlayer.getUuid().toString());
                if (customScoreboardOptional.isPresent()) {
                    CustomScoreboard customScoreboard = customScoreboardOptional.get();
                    customScoreboard.getEntry("PlayersTitle").update(ChatColor.WHITE + "Players " + ChatColor.GOLD + serverManager.getTotalPlayersOnline());
                }
            }));
    }

}
