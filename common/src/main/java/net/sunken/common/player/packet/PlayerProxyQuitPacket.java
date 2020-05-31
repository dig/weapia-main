package net.sunken.common.player.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

import java.util.UUID;

/**
 * Sent from bungeecords to master to
 * inform that a player has quit.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PlayerProxyQuitPacket extends Packet {

    private UUID uuid;

}
