package net.sunken.core.engine.state.impl;

import org.bukkit.entity.Player;

public abstract class BasePlayerState {

    protected Player player;
    public BasePlayerState(Player player) {
        this.player = player;
    }

    //--- Called on state start.
    public abstract void start(BasePlayerState previous);

    //--- Called on state stop, before switching.
    public abstract void stop(BasePlayerState next);

}
