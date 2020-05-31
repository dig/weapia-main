package net.sunken.common.packet.event;

import lombok.Getter;
import net.sunken.common.event.SunkenEvent;
import net.sunken.common.packet.Packet;

/**
 * Called when the server receives a deserialized packet
 * and will call this event with such packet. This works
 * even if the packet does not have a registered handler.
 *
 * @author Joseph Ali
 */
public class PacketReceivedEvent extends SunkenEvent {

    @Getter
    private Packet packet;

    public PacketReceivedEvent(Packet packet) {
        this.packet = packet;
    }

}
