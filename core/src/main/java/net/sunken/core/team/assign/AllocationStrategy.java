package net.sunken.core.team.assign;

import net.sunken.common.player.*;
import net.sunken.core.team.impl.*;

import java.util.*;

public interface AllocationStrategy {

    Set<Team> assign(Set<AbstractPlayer> players, Set<Team> teams);
}
