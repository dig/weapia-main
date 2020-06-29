package net.sunken.core.engine.state.impl;

import net.sunken.common.player.AbstractPlayer;
import org.bukkit.entity.Player;

public abstract class BasePlayerState {

    public abstract void start(AbstractPlayer abstractPlayer, BasePlayerState previous);
    public abstract void stop(AbstractPlayer abstractPlayer, BasePlayerState next);

}
