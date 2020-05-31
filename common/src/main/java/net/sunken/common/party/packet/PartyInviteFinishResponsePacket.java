package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;
import net.sunken.common.player.PlayerDetail;

import java.util.List;
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
public class PartyInviteFinishResponsePacket extends Packet {

    private PlayerDetail player;

    private UUID partyLeader;
    private List<UUID> partyMembers;

    private State state;

    public enum State {
        ALREADY_IN_PARTY,
        NO_INVITE,
        DENY,
        SUCCESS
    }

}
