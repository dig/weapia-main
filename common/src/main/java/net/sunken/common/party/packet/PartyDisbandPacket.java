package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

import java.util.UUID;

/**
 * Sent from any bungeecord to master to disband
 * current party.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartyDisbandPacket extends Packet {

    private UUID leader;

}
