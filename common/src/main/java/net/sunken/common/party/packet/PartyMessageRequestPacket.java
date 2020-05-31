package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;
import net.sunken.common.player.PlayerDetail;

/**
 * Sent from any bungeecord to master to request
 * a chat message.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartyMessageRequestPacket extends Packet {

    private PlayerDetail player;
    private String message;

}
