package net.sunken.common.player.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;
import net.sunken.common.player.PlayerDetail;

/**
 * Sent from bungeecords to master to
 * inform that a player has joined.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PlayerProxyJoinPacket extends Packet {

    private PlayerDetail player;

}
