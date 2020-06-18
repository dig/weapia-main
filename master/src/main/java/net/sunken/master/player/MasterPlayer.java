package net.sunken.master.player;

import lombok.NonNull;
import net.sunken.common.player.AbstractPlayer;

import java.util.UUID;

public class MasterPlayer extends AbstractPlayer {

    public MasterPlayer(@NonNull UUID uuid, @NonNull String username) {
        super(uuid, username);
    }

    public MasterPlayer(@NonNull UUID uuid) {
        super(uuid, null);
    }

    @Override
    public void setup() {
    }

    @Override
    public void destroy() {
    }

}
