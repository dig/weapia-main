package net.sunken.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Game {

    UNKNOWN ("Unknown", 0),
    NONE ("None", 80),

    ICE_RUNNER_SOLO ("Ice Runner (Solo)", 4),

    SURVIVAL_REALMS ("Survival Realms", 50),
    SURVIVAL_REALMS_ADVENTURE ("Survival Realms (Adventure)", 100);

    private String friendlyName;
    private int maxPlayers;

}
