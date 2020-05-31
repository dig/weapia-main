package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;
import net.sunken.common.player.PlayerDetail;

import java.util.Optional;
import java.util.UUID;

/**
 * Sent from master back to bungeecord
 * to tell them about the response of
 * the party invitation.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartyInviteResponsePacket extends Packet {

    private PlayerDetail inviter;

    private String target;
    private UUID targetUUID;

    private PartyInviteStatus partyInviteStatus;

    public enum PartyInviteStatus {
        NOT_IN_PARTY,
        NO_PERMISSION,

        TARGET_NOT_FOUND,
        TARGET_SELF,
        TARGET_ALREADY_INVITED,
        TARGET_ALREADY_IN_PARTY,

        SUCCESS
    }

}
