package net.sunken.master.boot;

import net.sunken.common.inject.*;
import net.sunken.common.master.*;
import net.sunken.common.packet.*;

import javax.inject.*;

public class MasterBootNotifier implements Enableable, Facet {

    @Inject
    private PacketUtil packetUtil;

    @Override
    public void enable() {
        packetUtil.send(new MasterBootPacket());
    }
}
