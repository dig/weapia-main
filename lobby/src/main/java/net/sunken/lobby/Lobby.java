package net.sunken.lobby;

import net.sunken.common.server.Server;
import net.sunken.core.Core;
import net.sunken.core.engine.EngineManager;
import net.sunken.core.engine.GameMode;
import net.sunken.lobby.state.LobbyState;

public class Lobby extends Core {

    @Override
    public void onEnable() {
        super.onEnable(new LobbyPluginModule(this));

        //--- Engine
        EngineManager engineManager = injector.getInstance(EngineManager.class);
        engineManager.setGameMode(GameMode.builder()
                .isStateTicking(true)
                .initialState(() -> injector.getInstance(LobbyState.class))
                .build());

        //--- Change state to open
        pluginInform.setState(Server.State.OPEN);
    }

}
