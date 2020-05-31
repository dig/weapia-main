package net.sunken.common.server.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;

/**
 * Sent from DEVELOPER+ to request a server.
 * Admin packet.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class RequestServerCreationPacket extends Packet {

    private Server.Type type;
    private Game game;

}
