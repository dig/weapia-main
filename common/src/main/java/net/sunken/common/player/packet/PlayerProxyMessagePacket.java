package net.sunken.common.player.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

import java.util.UUID;

/**
 * Sent from master to all bungeecords to
 * send a message to a player.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PlayerProxyMessagePacket extends Packet {

    private UUID target;
    private String message;

}
