package net.sunken.common.player;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public abstract class AbstractPlayer {

    protected final UUID uuid;
    protected final String username;
    protected Rank rank;

    public AbstractPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.rank = Rank.PLAYER;
    }

    /**
     * Blocking method to load player.
     */
    public abstract boolean load();

    /**
     * Blocking method to save player.
     */
    public abstract boolean save();

    /**
     * Called on successful join.
     */
    public abstract void setup();

    /**
     * Called on successful leave.
     */
    public abstract void destroy();

    public PlayerDetail toPlayerDetail() {
        return new PlayerDetail(uuid, username, rank);
    }

}
