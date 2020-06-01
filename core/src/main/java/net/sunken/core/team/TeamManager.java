package net.sunken.core.team;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.istack.internal.NotNull;
import lombok.extern.java.Log;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.core.team.config.TeamConfiguration;
import net.sunken.core.team.impl.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Log
@Singleton
public class TeamManager implements Facet, Enableable, Listener {

    @Inject @InjectConfig
    private TeamConfiguration teamConfiguration;
    @Inject
    private PlayerManager playerManager;

    private Set<Team> teamsList;

    public TeamManager() {
        this.teamsList = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
        teamsList.clear();
    }

    public Optional<Team> getByMemberUUID(@NotNull UUID uuid) {
        return teamsList.stream()
                .filter(team -> team.getMembers().contains(uuid))
                .findFirst();
    }

    public void assignOnlinePlayers(Queue<? extends Team> teams) {
        Queue<UUID> playersLeftToAssign = playerManager.getOnlinePlayers().stream()
                                                .map(AbstractPlayer::getUuid)
                                                .collect(Collectors.toCollection(LinkedList::new));

        while (!teams.isEmpty() && !playersLeftToAssign.isEmpty()) {
            Team team = teams.poll();

            while (team.getMembers().size() < teamConfiguration.getPlayersPerTeam() && !playersLeftToAssign.isEmpty()) {
                UUID uuid = playersLeftToAssign.poll();
                team.addMember(uuid);
                log.info(String.format("TeamManager: Assigning %s to team colour %s.", uuid.toString(), team.getColour().toString()));
            }

            if (team.getMembers().size() > 0) {
                teamsList.add(team);
            }
        }
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
