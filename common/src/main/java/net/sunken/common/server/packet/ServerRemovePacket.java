package net.sunken.common.server.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

/**
 * Sent from the server to master to inform master
 * that such server is being shutdown.
 *
 * @author Joseph Ali
 */
@AllArgsConstructor
public class ServerRemovePacket extends Packet {

    @Getter
    private String id;

}
