package net.sunken.core.player.simple;

import com.google.inject.Inject;
import net.sunken.core.scoreboard.ScoreboardRegistry;

import java.util.UUID;

public class MinigamePlayerFactory {

    @Inject
    private ScoreboardRegistry scoreboardRegistry;

    public MinigamePlayer createPlayer(UUID uuid, String username) {
        return new MinigamePlayer(uuid, username, scoreboardRegistry);
    }

}
