package net.sunken.core.engine;

import lombok.Builder;
import lombok.Data;
import net.sunken.common.server.Game;
import net.sunken.core.engine.state.impl.BaseGameState;

import java.util.function.Supplier;

@Data
@Builder
public class GameMode {

    private boolean isStateTicking;
    private Supplier<BaseGameState> initialState;

}
