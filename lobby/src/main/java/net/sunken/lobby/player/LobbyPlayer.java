package net.sunken.lobby.player;

import lombok.extern.java.Log;
import net.sunken.common.server.ServerHelper;
import net.sunken.common.server.module.ServerManager;
import net.sunken.core.PluginInform;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.ScoreboardManager;
import net.sunken.core.scoreboard.ScoreboardWrapper;
import net.sunken.core.util.ColourUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log
public class LobbyPlayer extends CorePlayer {

    private PluginInform pluginInform;
    private ScoreboardWrapper scoreboardWrapper;

    public LobbyPlayer(UUID uuid, String username, PluginInform pluginInform, ScoreboardManager scoreboardManager) {
        super(uuid, username, scoreboardManager);

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
        Optional<? extends Player> playerOptional = toPlayer();

        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();

            player.getInventory().clear();

            //--- Scoreboard
            Map<String, String> serverMetadata = pluginInform.getServer().getMetadata();
            String title = ChatColor.AQUA + "Lobby #"
                    + (serverMetadata.containsKey(ServerHelper.SERVER_METADATA_ID_KEY) ? serverMetadata.get(ServerHelper.SERVER_METADATA_ID_KEY) : "Pending.");

            scoreboardWrapper = new ScoreboardWrapper(title, scoreboardManager);
            scoreboardWrapper.add("Spacer1", ChatColor.WHITE + " ", 10);

            scoreboardWrapper.add("CreditsTitle", ChatColor.WHITE + " \u2996 Credits", 9);
            scoreboardWrapper.add("CreditsValue", ChatColor.LIGHT_PURPLE + " 0", 8);
            scoreboardWrapper.add("Spacer2", ChatColor.WHITE + " ", 7);

            scoreboardWrapper.add("RankTitle", ChatColor.WHITE + " \u2996 Rank", 6);
            scoreboardWrapper.add("RankValue", ColourUtil.fromColourCode(rank.getColourCode()) + " " + rank.getFriendlyName(), 5);
            scoreboardWrapper.add("Spacer3", ChatColor.WHITE + " ", 4);

            scoreboardWrapper.add("PlayTitle", ChatColor.WHITE + " \u2996 Play Streak", 3);
            scoreboardWrapper.add("PlayValue", ChatColor.GOLD + " 5 days", 2);
            scoreboardWrapper.add("Spacer4", ChatColor.WHITE + " ", 1);

            scoreboardWrapper.add("URL", ChatColor.GRAY + "play.weapia.com", 0);

            scoreboardWrapper.add(player);
        }

        super.setup();
    }

    @Override
    public void destroy() {
        scoreboardWrapper.getEntries().values().forEach(scoreboardEntry -> scoreboardEntry.remove());
    }

    @Override
    public ScoreboardWrapper getScoreboardWrapper() {
        return scoreboardWrapper;
    }

    public void onServerUpdate() {
        Map<String, String> serverMetadata = pluginInform.getServer().getMetadata();
        String title = ChatColor.AQUA + "Lobby #"
                + (serverMetadata.containsKey(ServerHelper.SERVER_METADATA_ID_KEY) ? serverMetadata.get(ServerHelper.SERVER_METADATA_ID_KEY) : "Pending.");

        scoreboardWrapper.setTitle(title);
    }


}
