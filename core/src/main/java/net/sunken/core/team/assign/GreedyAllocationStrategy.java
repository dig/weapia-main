package net.sunken.core.team.assign;

import lombok.extern.java.*;
import net.sunken.common.player.*;
import net.sunken.core.team.config.*;
import net.sunken.core.team.impl.*;

import java.util.*;
import java.util.stream.*;

@Log
public class GreedyAllocationStrategy implements AllocationStrategy {

    private final TeamConfiguration teamConfiguration;

    public GreedyAllocationStrategy(TeamConfiguration teamConfiguration) {
        this.teamConfiguration = teamConfiguration;
    }

    @Override
    public Set<Team> assign(Set<AbstractPlayer> players, Set<Team> teams) {
        Set<Team> result = new HashSet<>();

        Queue<Team> teamsLeft = new LinkedList<>(teams);
        Queue<UUID> playersLeftToAssign = players.stream()
                .map(AbstractPlayer::getUuid)
                .collect(Collectors.toCollection(LinkedList::new));

        while (!teamsLeft.isEmpty() && !playersLeftToAssign.isEmpty()) {
            Team team = teamsLeft.poll();

            while (team.getMembers().size() < teamConfiguration.getPlayersPerTeam() && !playersLeftToAssign.isEmpty()) {
                UUID uuid = playersLeftToAssign.poll();
                team.addMember(uuid);
                log.info(String.format("TeamManager: Assigning %s to team colour %s.", uuid.toString(), team.getColour().toString()));
            }

            if (team.getMembers().size() > 0) {
                result.add(team);
            }
        }

        return result;
    }
}
