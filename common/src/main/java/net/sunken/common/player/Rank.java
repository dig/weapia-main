package net.sunken.common.player;

import lombok.Getter;

@Getter
public enum Rank {

    PLAYER (0, 4, "Player", "&7"),
    MOD (5, 3, "Mod", "&2"),
    ADMIN (6, 2, "Admin", "&4"),
    DEVELOPER (7, 1, "Developer", "&c"),
    OWNER (8, 0, "Owner", "&4");

    private int index;
    private int order;
    private String friendlyName;
    private String colourCode;

    Rank(int index, int order, String friendlyName, String colourCode) {
        this.index = index;
        this.order = order;
        this.friendlyName = friendlyName;
        this.colourCode = colourCode;
    }

    public boolean has(Rank rank) {
        return index >= rank.getIndex();
    }

    public String constructName() {
        return this.colourCode + this.friendlyName;
    }

}
