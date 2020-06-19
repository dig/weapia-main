package net.sunken.master.reboot;

import net.sunken.common.inject.*;
import net.sunken.common.master.*;
import net.sunken.common.packet.*;

import javax.inject.*;

public class MasterRebootNotifier implements Enableable, Facet {

    @Inject
    private PacketUtil packetUtil;

    @Override
    public void enable() {
        packetUtil.send(new MasterRebootPacket());
    }

    @Override
    public void disable() {
    }
}
