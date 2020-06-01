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
    protected int maxPlayers;
    @Getter
    protected Set<UUID> members;
    @Getter
    protected BaseTeamState state;

    public Team(@NonNull ChatColor colour, int maxPlayers, BaseTeamState state) {
        this.colour = colour;
        this.maxPlayers = maxPlayers;
        this.members = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.setState(state);
    }

    public Team(@NonNull ChatColor colour, int maxPlayers) {
        this(colour, maxPlayers, null);
    }

    public void addMember(@NonNull UUID uuid) {
        if (members.add(uuid)) {
            this.state.onJoin(uuid);
        }
    }

    public void removeMember(@NonNull UUID uuid) {
        if (members.remove(uuid)) {
            this.state.onQuit(uuid);
        }
    }

    public void setState(BaseTeamState newState) {
        if (state != null) {
            state.stop(newState);
        }

        newState.start(state);
        state = newState;
    }

}
