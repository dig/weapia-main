package net.sunken.common.player;

import lombok.Getter;

@Getter
public enum Rank {

    PLAYER (0, "Player", "&7"),
    IMMORTAL (3, "Immortal", "&b"),
    RECRUIT (4, "Recruit", "&e"),
    MOD (5, "Mod", "&2"),
    ADMIN (6, "Admin", "&4"),
    DEVELOPER (7, "Developer", "&c"),
    OWNER (8, "Owner", "&4");

    private int index;
    private String friendlyName;
    private String colourCode;

    Rank(int index, String friendlyName, String colourCode) {
        this.index = index;
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
