package net.sunken.common.server.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

/**
 * Sent from the server to master to inform master that
 * such server is ready to be added to the network.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class ServerAddPacket extends Packet {

    private String id;

}
