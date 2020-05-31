package net.sunken.bungeecord.chat.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;
import net.sunken.common.player.PlayerDetail;
import net.sunken.common.player.Rank;

/**
 * Sent from any bungeecord to all bungeecords
 * to inform about an incoming staff message.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class StaffMessagePacket extends Packet {

    private PlayerDetail sender;

    private Rank target;
    private String message;

}