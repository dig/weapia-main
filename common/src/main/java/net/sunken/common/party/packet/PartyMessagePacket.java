package net.sunken.common.party.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;
import net.sunken.common.player.PlayerDetail;

import java.util.List;
import java.util.UUID;

/**
 * Sent from master to all bungeecords to send
 * a chat message to a party.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartyMessagePacket extends Packet {

    private PlayerDetail sender;
    private String message;

    private List<UUID> targets;

}
