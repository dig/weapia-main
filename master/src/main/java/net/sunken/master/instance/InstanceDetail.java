package net.sunken.master.instance;

import lombok.Data;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;

@Data
public class InstanceDetail {

    private final Server.Type type;
    private final Game game;

}
