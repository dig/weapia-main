package net.sunken.lobby.player;

import lombok.extern.java.Log;
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

            Map<String, String> serverMetadata = pluginInform.getServer().getMetadata();
            String title = ChatColor.AQUA + "" + ChatColor.BOLD + "Lobby #"
                    + (serverMetadata.containsKey(ServerHelper.SERVER_METADATA_ID_KEY) ? serverMetadata.get(ServerHelper.SERVER_METADATA_ID_KEY) : "Pending.");

            CustomScoreboard customScoreboard = new CustomScoreboard(title);
            customScoreboard.createEntry("Spacer1", ChatColor.WHITE + " ", 5);

            customScoreboard.createEntry("RankTitle", ChatColor.WHITE + "Rank " + ColourUtil.fromColourCode(rank.getColourCode()) + "" + rank.getFriendlyName(), 4);
            customScoreboard.createEntry("PlayersTitle", ChatColor.WHITE + "Players " + ChatColor.GREEN + serverManager.getTotalPlayersOnline(), 3);

            customScoreboard.createEntry("Spacer3", ChatColor.RED + " ", 2);
            customScoreboard.createEntry("ServerID", ChatColor.GRAY + pluginInform.getServer().getId(), 1);
            customScoreboard.createEntry("URL", ChatColor.AQUA + "play.weapia.c" + ChatColor.AQUA + "om", 0);

            customScoreboard.add(player);
            scoreboardRegistry.register(player.getUniqueId().toString(), customScoreboard);
        }
    }

    @Override
    public void destroy() {
        scoreboardRegistry.unregister(this.uuid.toString());
    }

}
