package net.sunken.lobby.player;

import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.player.Rank;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.util.Symbol;
import net.sunken.core.PluginInform;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.CustomScoreboard;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@Log
public class LobbyPlayer extends CorePlayer {

    private ServerManager serverManager;

    public LobbyPlayer(UUID uuid, String username, ServerManager serverManager, PluginInform pluginInform, ScoreboardRegistry scoreboardRegistry) {
        super(uuid, username, scoreboardRegistry, pluginInform);
        this.serverManager = serverManager;
    }

    @Override
    public void setup(@NonNull Player player) {
        super.setup(player);
        player.getInventory().clear();
    }

    @Override
    protected boolean setupScoreboard(@NonNull CustomScoreboard scoreboard) {
        scoreboard.createEntry("Spacer1", ChatColor.WHITE + " ", 11);

        scoreboard.createEntry("RankTitle", ChatColor.WHITE + "Rank", 10);
        scoreboard.createEntry("RankValue", rank == Rank.PLAYER ? ChatColor.RED + "No Rank" : ChatColor.valueOf(rank.getColour()) + "" + rank.getFriendlyName(), 9);
        scoreboard.createEntry("Spacer2", ChatColor.BLACK + " ", 8);

        scoreboard.createEntry("PlayersTitle", ChatColor.WHITE + "Players", 4);
        scoreboard.createEntry("PlayersValue", ChatColor.YELLOW + "" + String.format("%,d", serverManager.getTotalPlayersOnline()), 3);
        scoreboard.createEntry("Spacer4", ChatColor.YELLOW + " ", 2);
        return true;
    }
}
