package net.sunken.core.engine;

import lombok.Builder;
import lombok.Data;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.util.Tuple;
import net.sunken.core.engine.state.impl.BaseGameState;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Data
@Builder
public class GameMode {

    private boolean isStateTicking;
    private Supplier<BaseGameState> initialState;
    private Function<Tuple<UUID, String>, AbstractPlayer> playerMapper;

}
