package net.sunken.core.engine.state.impl;

import net.sunken.common.event.SunkenListener;
import org.bukkit.event.Listener;

public abstract class BaseGameState extends BaseState implements Listener, SunkenListener {

    public abstract void tick(int tickCount);

}
