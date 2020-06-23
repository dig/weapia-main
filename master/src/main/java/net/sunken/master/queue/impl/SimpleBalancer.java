package net.sunken.master.queue.impl;

import net.sunken.common.packet.PacketUtil;
import net.sunken.common.server.module.ServerManager;
import net.sunken.master.instance.InstanceManager;
import net.sunken.master.party.PartyManager;

public class SimpleBalancer extends AbstractBalancer {

    public SimpleBalancer(PartyManager partyManager, ServerManager serverManager, InstanceManager instanceManager, PacketUtil packetUtil) {
        super(partyManager, serverManager, instanceManager, packetUtil);
    }
}
