package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

import java.util.UUID;

/**
 * Sent from master back to bungeecord
 * to tell them about the response of
 * the party creation.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartyCreateResponsePacket extends Packet {

    private UUID uuid;
    private PartyCreateStatus partyCreateStatus;

    public enum PartyCreateStatus {
        ALREADY_IN_PARTY,
        SUCCESS
    }

}
