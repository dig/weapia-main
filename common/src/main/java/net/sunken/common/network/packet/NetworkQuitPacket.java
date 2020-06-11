package net.sunken.common.network.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;
import net.sunken.common.player.PlayerDetail;

import java.util.UUID;

/**
 * Sent from bungeecords to master to
 * inform that a player has quit.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class NetworkQuitPacket extends Packet {

    private PlayerDetail player;

}
