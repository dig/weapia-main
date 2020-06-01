package net.sunken.core.team.allocate;

import lombok.extern.java.*;
import net.sunken.common.player.*;
import net.sunken.core.team.impl.*;

import java.util.*;

@Log
public class GreedyAllocationStrategy implements AllocationStrategy {

    @Override
    public Allocation allocate(Set<AbstractPlayer> players, Set<Team> teams) {
        Set<Team> resultantTeams = new HashSet<>();
        Queue<Team> teamsLeft = new LinkedList<>(teams);
        Queue<AbstractPlayer> unallocatedPlayers = new LinkedList<>(players);

        while (!teamsLeft.isEmpty() && !unallocatedPlayers.isEmpty()) {
            Team team = teamsLeft.poll();

            while (team.getMembers().size() < team.getMaxPlayers() && !unallocatedPlayers.isEmpty()) {
                UUID uuid = unallocatedPlayers.poll().getUuid();
                team.addMember(uuid);
                log.info(String.format("TeamManager: Assigning %s to team colour %s.", uuid.toString(), team.getColour().toString()));
            }

            if (team.getMembers().size() > 0) {
                resultantTeams.add(team);
            }
        }

        return new Allocation(resultantTeams, new HashSet<>(unallocatedPlayers));
    }
}
