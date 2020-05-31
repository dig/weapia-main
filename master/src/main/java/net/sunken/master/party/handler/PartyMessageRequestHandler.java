package net.sunken.master.party.handler;

import com.google.inject.Inject;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.party.packet.PartyMessagePacket;
import net.sunken.common.party.packet.PartyMessageRequestPacket;
import net.sunken.master.party.Party;
import net.sunken.master.party.PartyManager;

import java.util.Optional;

public class PartyMessageRequestHandler extends PacketHandler<PartyMessageRequestPacket> {

    @Inject
    private PartyManager partyManager;
    @Inject
    private PacketUtil packetUtil;

    @Override
    public void onReceive(PartyMessageRequestPacket packet) {
        Optional<Party> partyOptional = partyManager.findPartyByMember(packet.getPlayer().getUuid());
        if (!partyOptional.isPresent()) return;

        Party party = partyOptional.get();
        packetUtil.send(new PartyMessagePacket(packet.getPlayer(), packet.getMessage(), party.getMembersAsUuid()));
    }

}
