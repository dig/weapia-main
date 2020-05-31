package net.sunken.common.packet.expectation;

import lombok.extern.java.Log;
import net.sunken.common.event.ListensToEvent;
import net.sunken.common.event.SunkenListener;
import net.sunken.common.packet.Packet;
import net.sunken.common.packet.event.PacketReceivedEvent;

import java.util.function.Predicate;

@Log
public class Expectation implements SunkenListener {

    private final Predicate<Packet> condition;
    private volatile boolean isMet = false;

    public Expectation(Predicate<Packet> condition) {
        this.condition = condition;
    }

    @ListensToEvent
    public void onPacketReceived(PacketReceivedEvent event) {
        if (condition.test(event.getPacket())) {
            setMet();
        }
    }

    public boolean isMet() {
        return isMet;
    }

    public void setMet() {
        isMet = true;
    }

}