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
    public boolean load() {
        return true;
    }

    @Override
    public boolean save() {
        return true;
    }

    @Override
    public void setup() {
        super.setup();

        Optional<? extends Player> playerOptional = toPlayer();
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            player.getInventory().clear();

            CustomScoreboard customScoreboard = new CustomScoreboard(ChatColor.AQUA + "" + ChatColor.BOLD + "WEAPIA");
            customScoreboard.createEntry("Spacer1", ChatColor.WHITE + " ", 10);

            customScoreboard.createEntry("RankTitle", ChatColor.WHITE + "Rank", 9);
            customScoreboard.createEntry("RankValue", rank == Rank.PLAYER ? ChatColor.RED + "No Rank /join" : ColourUtil.fromColourCode(rank.getColourCode()) + "" + rank.getFriendlyName(), 8);
            customScoreboard.createEntry("Spacer2", ChatColor.BLACK + " ", 7);

            customScoreboard.createEntry("EventsTitle", ChatColor.WHITE + "Events", 6);
            customScoreboard.createEntry("EventsValue", ChatColor.LIGHT_PURPLE + "2x /vote", 5);
            customScoreboard.createEntry("Spacer3", ChatColor.RED + " ", 4);

            customScoreboard.createEntry("PlayersTitle", ChatColor.WHITE + "Players", 3);
            customScoreboard.createEntry("PlayersValue", ChatColor.YELLOW + "" + serverManager.getTotalPlayersOnline(), 2);

            customScoreboard.createEntry("Spacer4", ChatColor.YELLOW + " ", 1);
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
