package net.sunken.core.team;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.*;
import lombok.extern.java.Log;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.core.team.allocate.*;
import net.sunken.core.team.config.TeamConfiguration;
import net.sunken.core.team.impl.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Log
@Singleton
public class TeamManager implements Facet, Enableable, Listener {

    @Inject @InjectConfig
    private TeamConfiguration teamConfiguration;
    @Inject
    private PlayerManager playerManager;

    @Setter
    private AllocationStrategy allocationStrategy;
    private Set<Team> teamsList;

    public TeamManager() {
        this.teamsList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public void enable() {
        allocationStrategy = new GreedyAllocationStrategy();
    }

    @Override
    public void disable() {
        teamsList.clear();
    }

    public Optional<Team> getByMemberUUID(@NonNull UUID uuid) {
        return teamsList.stream()
                .filter(team -> team.getMembers().contains(uuid))
                .findFirst();
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
