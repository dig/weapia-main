package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;
import net.sunken.common.player.PlayerDetail;

import java.util.UUID;

/**
 * Sent from any bungeecord to master to invite
 * a new player to a party.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartyInvitePacket extends Packet {

    private PlayerDetail inviter;
    private String target;

}
