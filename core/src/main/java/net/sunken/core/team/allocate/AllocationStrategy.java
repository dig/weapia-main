package net.sunken.core.team.allocate;

import net.sunken.common.player.*;
import net.sunken.core.team.impl.*;

import java.util.*;

public interface AllocationStrategy {

    Allocation allocate(Set<AbstractPlayer> players, Set<Team> teams);
}
