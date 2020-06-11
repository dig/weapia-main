package net.sunken.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Game {

    NONE ("None", 80, null, null),
    ICE_RUNNER_SOLO ("Ice Runner (Solo)", 4, "respects/wep-infrastructure:icerunner-solo", "respects/wep-infrastructure-dev:icerunner-solo"),

    ORE_WARS_SOLO ("Ore Wars (Solo)", 6, "respects/wep-infrastructure:ore-wars-solo", "respects/wep-infrastructure-dev:ore-wars-solo"),
    ORE_WARS_TEAM ("Ore Wars (Teams)", 12, "respects/wep-infrastructure:ore-wars-team", "respects/wep-infrastructure-dev:ore-wars-team");

    private String friendlyName;
    private int maxPlayers;
    private String prodImageUri;
    private String devImageUri;

}
