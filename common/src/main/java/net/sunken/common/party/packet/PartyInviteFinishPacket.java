package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

import java.util.UUID;

/**
 * Sent from any bungeecord to master to finish
 * invite process.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartyInviteFinishPacket extends Packet {

    private UUID uuid;
    private String target;

    private State state;

    public enum State {
        DENY,
        ACCEPT
    }

}
