package net.sunken.lobby;

import net.sunken.common.server.Server;
import net.sunken.core.Core;
import net.sunken.core.engine.EngineManager;
import net.sunken.core.engine.GameMode;
import net.sunken.lobby.player.LobbyPlayerFactory;
import net.sunken.lobby.state.LobbyState;

public class Lobby extends Core {

    @Override
    public void onEnable() {
        super.onEnable(new LobbyPluginModule(this));

        EngineManager engineManager = injector.getInstance(EngineManager.class);
        LobbyPlayerFactory lobbyPlayerFactory = injector.getInstance(LobbyPlayerFactory.class);

        engineManager.setGameMode(GameMode.builder()
                .isStateTicking(true)
                .initialState(() -> injector.getInstance(LobbyState.class))
                .playerMapper(uuidStringTuple -> lobbyPlayerFactory.createPlayer(uuidStringTuple.getX(), uuidStringTuple.getY()))
                .build());

        pluginInform.setState(Server.State.OPEN);
    }
}
