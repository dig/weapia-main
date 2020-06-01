package net.sunken.core.engine.state.impl;

import net.sunken.core.team.impl.Team;

import java.util.UUID;

public abstract class BaseTeamState {

    protected Team team;
    public BaseTeamState(Team team) { this.team = team; }

    //--- Called on state start.
    public abstract void start(BaseTeamState previous);

    //--- Called on state stop, before switching.
    public abstract void stop(BaseTeamState next);

    //--- Called when a player joins the team.
    public abstract void onJoin(UUID uuid);

    //--- Called when a player leaves the team.
    public abstract void onQuit(UUID uuid);

}
