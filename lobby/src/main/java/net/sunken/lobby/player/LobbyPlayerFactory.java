package net.sunken.lobby.player;

import com.google.inject.Inject;
import net.sunken.common.server.module.ServerManager;
import net.sunken.core.PluginInform;
import net.sunken.core.scoreboard.ScoreboardRegistry;

import java.util.UUID;

public class LobbyPlayerFactory {

    @Inject
    private PluginInform pluginInform;
    @Inject
    private ServerManager serverManager;
    @Inject
    private ScoreboardRegistry scoreboardRegistry;

    public LobbyPlayer createPlayer(UUID uuid, String username) {
        return new LobbyPlayer(uuid, username, serverManager, pluginInform, scoreboardRegistry);
    }

}
