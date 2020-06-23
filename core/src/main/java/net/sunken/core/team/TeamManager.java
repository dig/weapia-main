package net.sunken.core.team;

import com.google.common.collect.Sets;
import com.google.inject.*;
import lombok.*;
import lombok.extern.java.*;
import net.sunken.common.config.*;
import net.sunken.common.inject.*;
import net.sunken.common.player.PlayerManager;
import net.sunken.core.engine.state.impl.BaseTeamState;
import net.sunken.core.team.allocate.*;
import net.sunken.core.team.config.*;
import net.sunken.core.team.impl.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Log
@Singleton
public class TeamManager implements Facet, Enableable, Listener {

    @Inject @InjectConfig
    private TeamConfiguration teamConfiguration;
    @Setter
    private Function<TeamSingleConfiguration, Team> teamConfigMapper;

    @Inject
    private PlayerManager playerManager;

    @Setter
    private AllocationStrategy allocationStrategy;
    @Getter
    private Set<Team> teamsList = Sets.newHashSet();

    @Override
    public void enable() {
        allocationStrategy = new GreedyAllocationStrategy();
    }

    public void allocateTeams() {
        if (allocationStrategy != null && teamConfigMapper != null) {
            Set<Team> teams = teamConfiguration.getTeamsAvailable().stream()
                    .map(teamConfigMapper)
                    .collect(Collectors.toSet());

            Allocation allocation = allocationStrategy.allocate(playerManager.getOnlinePlayers(), teams);
            teamsList = allocation.getResultantTeams();
        }
    }

    public Optional<Team> get(@NonNull String id) {
        return teamsList.stream()
                .filter(team -> team.getId().equals(id))
                .findFirst();
    }


    public Optional<Team> getByMemberUUID(@NonNull UUID uuid) {
        return teamsList.stream()
                .filter(team -> team.getMembers().contains(uuid))
                .findFirst();
    }

    public Set<Team> hasState(Class<? extends BaseTeamState> state) {
        return teamsList.stream()
                .filter(team -> team.getState() != null)
                .filter(team -> team.getState().getClass().isAssignableFrom(state))
                .collect(Collectors.toSet());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<Team> teamOptional = getByMemberUUID(player.getUniqueId());

        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            team.removeMember(player.getUniqueId());
        }
    }

}
