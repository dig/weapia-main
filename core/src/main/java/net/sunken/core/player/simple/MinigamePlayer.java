package net.sunken.core.player.simple;

import lombok.NonNull;
import net.sunken.core.PluginInform;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.CustomScoreboard;
import net.sunken.core.scoreboard.ScoreboardRegistry;

import java.util.UUID;

public class MinigamePlayer extends CorePlayer {

    public MinigamePlayer(UUID uuid, String username, ScoreboardRegistry scoreboardRegistry, PluginInform pluginInform) {
        super(uuid, username, scoreboardRegistry, pluginInform);
    }

    @Override
    protected boolean setupScoreboard(@NonNull CustomScoreboard scoreboard) {
        return false;
    }
}
