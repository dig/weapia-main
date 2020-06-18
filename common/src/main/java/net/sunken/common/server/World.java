package net.sunken.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum World {

    UNKNOWN ("Unknown"),
    NONE ("None"),

    LOBBY ("Lobby"),
    GAME_LOBBY ("Game Lobby"),

    ICERUNNER_PLAINS ("Plains"),
    SURVIVAL_REALMS_HUB ("Hub");

    private final String friendlyName;

}