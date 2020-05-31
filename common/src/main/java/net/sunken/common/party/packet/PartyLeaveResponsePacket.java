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
 * the party leave request.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartyLeaveResponsePacket extends Packet {

    private PlayerDetail player;
    private List<UUID> members;
    private Status status;

    public enum Status {
        NOT_IN_PARTY,
        LEADER,
        SUCCESS
    }

}
