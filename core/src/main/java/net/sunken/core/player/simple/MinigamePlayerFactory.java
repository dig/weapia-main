package net.sunken.core.player.simple;

import com.google.inject.Inject;
import net.sunken.core.PluginInform;
import net.sunken.core.scoreboard.ScoreboardRegistry;

import java.util.UUID;

public class MinigamePlayerFactory {

    @Inject
    private ScoreboardRegistry scoreboardRegistry;
    @Inject
    private PluginInform pluginInform;

    public MinigamePlayer createPlayer(UUID uuid, String username) {
        return new MinigamePlayer(uuid, username, scoreboardRegistry, pluginInform);
    }

}
