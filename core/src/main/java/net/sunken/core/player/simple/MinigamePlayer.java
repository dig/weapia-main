package net.sunken.core.player.simple;

import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.ScoreboardRegistry;

import java.util.UUID;

public class MinigamePlayer extends CorePlayer {

    public MinigamePlayer(UUID uuid, String username, ScoreboardRegistry scoreboardRegistry) {
        super(uuid, username, scoreboardRegistry);
    }
}
