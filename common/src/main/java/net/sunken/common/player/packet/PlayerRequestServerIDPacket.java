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
 * server for such Server ID.
 *
 * Master will respond with a PlayerSendToServerPacket with
 * an available server.
 *
 * @author Joseph Ali
 */
@Getter
public class PlayerRequestServerIDPacket extends Packet {

    private UUID uuid;
    private String serverID;

    public PlayerRequestServerIDPacket(@NonNull UUID uuid, @NonNull String serverID) {
        this.uuid = uuid;
        this.serverID = serverID;
    }

}
