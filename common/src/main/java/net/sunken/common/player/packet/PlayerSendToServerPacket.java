package net.sunken.common.player.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.sunken.common.packet.Packet;
import net.sunken.common.server.ServerDetail;

import java.util.UUID;

/**
 * Sent from master to every bungeecord to inform that
 * such player must be sent to such server. Usually sent
 * because a server has requested that such player
 * joins such Server.Type and Game.
 *
 * @author Joseph Ali
 */
@AllArgsConstructor
public class PlayerSendToServerPacket extends Packet {

    @Getter
    private UUID uuid;
    @Getter
    private ServerDetail serverDetail;

}
