package net.sunken.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Game {

    NONE ("None", 80, null, null);
    // EXAMPLE_GAME ("Example Game", 12, "respects/wep-infrastructure:examplegame", "respects/wep-infrastructure-dev:examplegame");

    private String friendlyName;
    private int maxPlayers;
    private String prodImageUri;
    private String devImageUri;

}
