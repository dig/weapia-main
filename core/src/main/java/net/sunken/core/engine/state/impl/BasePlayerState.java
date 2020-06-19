package net.sunken.core.engine.state.impl;

import net.sunken.common.player.AbstractPlayer;
import org.bukkit.entity.Player;

public abstract class BasePlayerState {

    //--- Called on state start.
    public abstract void start(AbstractPlayer abstractPlayer, BasePlayerState previous);

    //--- Called on state stop, before switching.
    public abstract void stop(AbstractPlayer abstractPlayer, BasePlayerState next);

}
