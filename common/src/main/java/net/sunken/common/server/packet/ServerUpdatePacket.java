package net.sunken.common.server.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

/**
 * Sent from the server to master to inform master
 * that such server has updated the redis cache.
 *
 * Master will now pull the updated server cache from redis
 * and store in local cache for internal purposes.
 *
 * @author Joseph Ali
 */
@AllArgsConstructor
public class ServerUpdatePacket extends Packet {

    @Getter
    private String id;
    @Getter
    private Type type;

    public enum Type {
        SERVER,
        METADATA
    }

}
