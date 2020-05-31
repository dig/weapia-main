package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

import java.util.List;
import java.util.UUID;

/**
 * Sent from master back to bungeecord
 * to tell them that party is disbanding.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartyDisbandResponsePacket extends Packet {

    private UUID leader;
    private List<UUID> members;

    private PartyDisbandStatus partyDisbandStatus;

    public enum PartyDisbandStatus {
        NOT_IN_PARTY,
        NO_PERMISSION,

        SUCCESS
    }

}
