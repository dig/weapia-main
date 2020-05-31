package net.sunken.master.instance;

import lombok.Getter;
import lombok.NonNull;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;

public class InstanceDetail {

    @Getter
    private Server.Type type;
    @Getter
    private Game game;

    public InstanceDetail(@NonNull Server.Type type, @NonNull Game game) {
        this.type = type;
        this.game = game;
    }

}
