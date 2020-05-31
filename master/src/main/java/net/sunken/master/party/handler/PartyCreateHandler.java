package net.sunken.master.party.handler;

import com.google.inject.Inject;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.party.packet.PartyCreatePacket;
import net.sunken.common.party.packet.PartyCreateResponsePacket;
import net.sunken.master.party.PartyManager;

public class PartyCreateHandler extends PacketHandler<PartyCreatePacket> {

    @Inject
    private PartyManager partyManager;
    @Inject
    private PacketUtil packetUtil;

    @Override
    public void onReceive(PartyCreatePacket packet) {
        PartyCreateResponsePacket.PartyCreateStatus partyCreateStatus = partyManager.createParty(packet.getLeader());
        packetUtil.send(new PartyCreateResponsePacket(packet.getLeader().getUuid(), partyCreateStatus));
    }

}
