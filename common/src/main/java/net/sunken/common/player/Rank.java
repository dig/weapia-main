package net.sunken.common.player;

import lombok.Getter;

@Getter
public enum Rank {

    PLAYER (0, 4, "Player", "GRAY"),
    MOD (5, 3, "Mod", "GREEN"),
    ADMIN (6, 2, "Admin", "DARK_RED"),
    DEVELOPER (7, 1, "Developer", "RED"),
    OWNER (8, 0, "Owner", "DARK_RED");

    private int index;
    private int order;
    private String friendlyName;
    private String colour;

    Rank(int index, int order, String friendlyName, String colour) {
        this.index = index;
        this.order = order;
        this.friendlyName = friendlyName;
        this.colour = colour;
    }

    public boolean has(Rank rank) {
        return index >= rank.getIndex();
    }

}
