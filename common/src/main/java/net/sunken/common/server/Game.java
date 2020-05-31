package net.sunken.common.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Game {

    NONE ("None", 80, null, null),
    SPACE_GAMES_SOLO ("Space Games (Solo)", 12, "respects/mv-infrastructure:spacegames", "respects/mv-infrastructure-dev:spacegames"),
    SPACE_GAMES_DUO ("Space Games (Duo)", 24, "respects/mv-infrastructure:spacegames", "respects/mv-infrastructure-dev:spacegames"),
    NATURAL_DISASTER ("Natural Disaster", 12, "respects/mv-infrastructure:naturaldisaster", "respects/mv-infrastructure-dev:naturaldisaster");

    private String friendlyName;
    private int maxPlayers;
    private String prodImageUri;
    private String devImageUri;

}
