package net.sunken.core.team.impl;

import lombok.*;
import net.sunken.core.engine.state.impl.*;
import org.bukkit.*;

import java.util.*;
import java.util.concurrent.*;

public abstract class Team {

    @Getter
    protected ChatColor colour;
    @Getter
    protected String displayName;
    @Getter
    protected int maxPlayers;
    @Getter
    protected Set<UUID> members;
    @Getter
    protected BaseTeamState state;

    public Team(@NonNull ChatColor colour, @NonNull String displayName, int maxPlayers, BaseTeamState state) {
        this.colour = colour;
        this.displayName = displayName;
        this.maxPlayers = maxPlayers;
        this.members = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.setState(state);
    }

    public Team(@NonNull ChatColor colour, @NonNull String displayName, int maxPlayers) {
        this(colour, displayName, maxPlayers, null);
    }

    public void addMember(@NonNull UUID uuid) {
        if (members.add(uuid)) {
            this.state.onJoin(this, uuid);
        }
    }

    public void removeMember(@NonNull UUID uuid) {
        if (members.remove(uuid)) {
            this.state.onQuit(this, uuid);
        }
    }

    public void setState(BaseTeamState newState) {
        if (state != null) {
            state.stop(this, newState);
        }

        newState.start(this, state);
        state = newState;
    }

}
