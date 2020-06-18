package net.sunken.lobby.player;

import lombok.extern.java.Log;
import net.sunken.common.player.Rank;
import net.sunken.common.server.ServerHelper;
import net.sunken.common.server.module.ServerManager;
import net.sunken.core.PluginInform;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.CustomScoreboard;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import net.sunken.core.util.ColourUtil;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log
public class LobbyPlayer extends CorePlayer {

    private ServerManager serverManager;
    private PluginInform pluginInform;

    public LobbyPlayer(UUID uuid, String username, ServerManager serverManager, PluginInform pluginInform, ScoreboardRegistry scoreboardRegistry) {
        super(uuid, username, scoreboardRegistry);
        this.serverManager = serverManager;
        this.pluginInform = pluginInform;
    }

    @Override
    public void setup() {
        super.setup();

        Optional<? extends Player> playerOptional = toPlayer();
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            player.getInventory().clear();

            CustomScoreboard customScoreboard = new CustomScoreboard(ChatColor.AQUA + "" + ChatColor.BOLD + "WEAPIA");
            customScoreboard.createEntry("Spacer1", ChatColor.WHITE + " ", 11);

            customScoreboard.createEntry("RankTitle", ChatColor.WHITE + "Rank", 10);
            customScoreboard.createEntry("RankValue", rank == Rank.PLAYER ? ChatColor.RED + "No Rank /join" : ColourUtil.fromColourCode(rank.getColourCode()) + "" + rank.getFriendlyName(), 9);
            customScoreboard.createEntry("Spacer2", ChatColor.BLACK + " ", 8);

            customScoreboard.createEntry("PlayersTitle", ChatColor.WHITE + "Players", 4);
            customScoreboard.createEntry("PlayersValue", ChatColor.YELLOW + "" + serverManager.getTotalPlayersOnline(), 3);

            customScoreboard.createEntry("Spacer4", ChatColor.YELLOW + " ", 2);
            customScoreboard.createEntry("ServerID", ChatColor.GRAY + pluginInform.getServer().getId(), 1);
            customScoreboard.createEntry("URL", ChatColor.LIGHT_PURPLE + "play.weapia.com", 0);

            customScoreboard.add(player);
            scoreboardRegistry.register(player.getUniqueId().toString(), customScoreboard);
        }
    }

    @Override
    public void destroy() {
        scoreboardRegistry.unregister(this.uuid.toString());
    }

}
