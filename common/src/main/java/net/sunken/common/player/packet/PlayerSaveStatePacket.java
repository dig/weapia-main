package net.sunken.common.player.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.packet.Packet;

import java.util.UUID;

/**
 * Sent from master to servers to inform server
 * that such player needs to save before connecting
 * to another server.
 *
 * Server will respond with such packet with
 * COMPLETE reason.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class PlayerSaveStatePacket extends Packet {

    private UUID uuid;
    private Reason reason;

    public enum Reason {
        REQUEST,
        COMPLETE,
        FAIL
    }

}
