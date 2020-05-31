package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

import java.util.UUID;

/**
 * Sent from any bungeecord to master to kick
 * a player from the party.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartyKickPacket extends Packet {

    private UUID instigator;
    private String target;

}
