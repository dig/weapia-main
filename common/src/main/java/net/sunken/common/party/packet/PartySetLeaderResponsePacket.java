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
 * the party leadership change.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PartySetLeaderResponsePacket extends Packet {

    private UUID player;
    private String target;

    private PlayerDetail leader;
    private List<UUID> members;

    private Status status;

    public enum Status {
        NOT_IN_PARTY,
        NO_PERMISSION,

        TARGET_NOT_FOUND,
        TARGET_SELF,
        TARGET_NOT_IN_PARTY,

        SUCCESS
    }

}
