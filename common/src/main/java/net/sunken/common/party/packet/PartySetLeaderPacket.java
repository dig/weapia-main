package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

import java.util.UUID;

/**
 * Sent from any bungeecord to master to change
 * the leader of the party.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartySetLeaderPacket extends Packet {

    private UUID uuid;
    private String target;

}
