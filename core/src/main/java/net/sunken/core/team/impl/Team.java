package net.sunken.core.team.impl;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.NonNull;
import net.sunken.core.engine.state.impl.BaseTeamState;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Team {

    @Getter
    protected ChatColor colour;
    @Getter
    protected Set<UUID> members;
    @Getter
    protected BaseTeamState state;

    public Team(@NonNull ChatColor colour, BaseTeamState state) {
        this.colour = colour;
        this.members  = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.setState(state);
    }

    public Team(@NonNull ChatColor colour) {
        this(colour, null);
    }

    public void addMember(@NotNull UUID uuid) {
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
