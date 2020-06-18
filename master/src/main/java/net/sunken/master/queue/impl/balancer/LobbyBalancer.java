package net.sunken.master.queue.impl.balancer;

import net.sunken.common.packet.PacketUtil;
import net.sunken.common.server.module.ServerManager;
import net.sunken.master.party.PartyManager;

public class LobbyBalancer extends AbstractBalancer {

    public LobbyBalancer(PartyManager partyManager, ServerManager serverManager, PacketUtil packetUtil) {
        super(partyManager, serverManager, packetUtil);
    }

}
