package net.sunken.core.team.allocate;

import lombok.*;
import net.sunken.common.player.*;
import net.sunken.core.team.impl.*;

import java.util.*;

@Data
public class Allocation {

    private final Set<Team> resultantTeams;
    private final Set<AbstractPlayer> unallocatedPlayers;
}
