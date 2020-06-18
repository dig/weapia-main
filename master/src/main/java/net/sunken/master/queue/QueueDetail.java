package net.sunken.master.queue;

import lombok.Data;
import net.sunken.common.player.PlayerDetail;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;

@Data
public class QueueDetail {

    private final PlayerDetail instigator;
    private final Server.Type type;
    private final Game game;

}
