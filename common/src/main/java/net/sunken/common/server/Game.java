package net.sunken.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Game {

    NONE ("None", 80, null, null),
    ICE_RUNNER_SOLO ("Ice Runner (Solo)", 4, "respects/wep-infrastructure:icerunner-solo", "respects/wep-infrastructure-dev:icerunner-solo");

    private String friendlyName;
    private int maxPlayers;
    private String prodImageUri;
    private String devImageUri;

}
