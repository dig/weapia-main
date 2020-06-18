package net.sunken.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Game {

    NONE ("None", 80, null, null),

    ICE_RUNNER_SOLO ("Ice Runner (Solo)", 4, "respects/wep-infrastructure:icerunner-solo", "respects/wep-infrastructure-dev:icerunner-solo"),

    SURVIVAL_REALMS ("Survival Realms", 50, "respects/wep-infrastructure:survival-realms", "respects/wep-infrastructure-dev:survival-realms"),
    SURVIVAL_REALMS_ADVENTURE ("Survival Realms (Adventure)", 100, "respects/wep-infrastructure:survival-realms-adventure", "respects/wep-infrastructure-dev:survival-realms-adventure");

    private String friendlyName;
    private int maxPlayers;
    private String prodImageUri;
    private String devImageUri;

}
