package net.sunken.master.queue;

import lombok.Data;
import net.sunken.common.player.PlayerDetail;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;

import java.util.UUID;

@Data
public class QueueDetail {

    private final UUID uuid;
    private final Server.Type type;
    private final Game game;

}
