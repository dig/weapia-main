package net.sunken.common.server.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

import java.util.UUID;

/**
 * Sent from the server to master to inform the master that a player
 * has successfully connected to such server.
 *
 * @author Joseph Ali
 */
@AllArgsConstructor
public class ServerConnectedPacket extends Packet {

    @Getter
    private UUID uuid;
    @Getter
    private String serverId;

}
