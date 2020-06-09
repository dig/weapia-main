package net.sunken.core.team.impl;

import com.google.common.collect.Sets;
import lombok.*;
import net.sunken.core.engine.state.impl.*;
import org.bukkit.*;

import java.util.*;

@Getter
public abstract class Team {

    protected final String id;
    protected final ChatColor colour;
    protected final String displayName;
    protected final int maxPlayers;
    protected final Set<UUID> members = Sets.newHashSet();
    protected BaseTeamState state;

    public Team(@NonNull String id, @NonNull ChatColor colour, @NonNull String displayName, int maxPlayers, BaseTeamState state) {
        this.id = id;
        this.colour = colour;
        this.displayName = displayName;
        this.maxPlayers = maxPlayers;
        this.setState(state);
    }

    public Team(@NonNull String id, @NonNull ChatColor colour, @NonNull String displayName, int maxPlayers) {
        this(id, colour, displayName, maxPlayers, null);
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
