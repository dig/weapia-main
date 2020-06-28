package net.sunken.common.player.packet;

import lombok.Getter;
import lombok.NonNull;
import net.sunken.common.packet.Packet;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;

import java.util.UUID;

/**
 * Sent from the server to master to inform master
 * that such player needs to connect to an available
 * server for such Server.Type and Game.
 *
 * Master will respond with a PlayerSendToServerPacket with
 * an available server.
 *
 * @author Joseph Ali
 */
@Getter
public class PlayerRequestServerPacket extends Packet {

    private UUID uuid;
    private Server.Type type;
    private Game game;
    private boolean save;

    public PlayerRequestServerPacket(@NonNull UUID uuid, @NonNull Server.Type type, @NonNull Game game, boolean save) {
        this.uuid = uuid;
        this.type = type;
        this.game = game;
        this.save = save;
    }

    public PlayerRequestServerPacket(@NonNull UUID uuid, @NonNull Server.Type type, boolean save) {
        this(uuid, type, Game.NONE, save);
    }
}
