package net.sunken.core.engine.state.impl;

import net.sunken.core.team.impl.Team;

import java.util.UUID;

public abstract class BaseTeamState {

    //--- Called on state start.
    public abstract void start(Team team, BaseTeamState previous);

    //--- Called on state stop, before switching.
    public abstract void stop(Team team, BaseTeamState next);

    //--- Called when a player joins the team.
    public abstract void onJoin(Team team, UUID uuid);

    //--- Called when a player leaves the team.
    public abstract void onQuit(Team team, UUID uuid);

}
