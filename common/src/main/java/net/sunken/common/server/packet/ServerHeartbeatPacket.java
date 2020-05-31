package net.sunken.common.server.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

/**
 * Sent from both server and master, master sends packet with
 * type REQUEST every interval and servers must respond with
 * this packet but with type RESPOND to successfully
 * respond to the heartbeat check.
 *
 * serverId will be null on the one sent from master.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class ServerHeartbeatPacket extends Packet {

    private String serverId;
    private Reason reason;

    public enum Reason {
        REQUEST,
        RESPOND
    }

}
