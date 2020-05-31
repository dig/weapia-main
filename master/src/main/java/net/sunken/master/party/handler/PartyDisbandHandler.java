package net.sunken.master.party.handler;

import com.google.inject.Inject;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.party.packet.PartyDisbandPacket;
import net.sunken.common.party.packet.PartyDisbandResponsePacket;
import net.sunken.common.player.PlayerDetail;
import net.sunken.master.party.Party;
import net.sunken.master.party.PartyManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyDisbandHandler extends PacketHandler<PartyDisbandPacket> {

    @Inject
    private PartyManager partyManager;
    @Inject
    private PacketUtil packetUtil;

    @Override
    public void onReceive(PartyDisbandPacket packet) {
        Optional<Party> partyOptional = partyManager.findPartyByMember(packet.getLeader());

        //--- In party?
        if (!partyOptional.isPresent()) {
            respond(packet.getLeader(), null, PartyDisbandResponsePacket.PartyDisbandStatus.NOT_IN_PARTY);
            return;
        }

        Party party = partyOptional.get();

        //--- Permission
        if (party.getLeader().isPresent()) {
            PlayerDetail leader = party.getLeader().get();

            if (!leader.getUuid().equals(packet.getLeader())) {
                respond(packet.getLeader(), null, PartyDisbandResponsePacket.PartyDisbandStatus.NO_PERMISSION);
                return;
            }
        }

        //--- Disband
        if (partyManager.disband(party)) {
            respond(packet.getLeader(), party.getMembersAsUuid(), PartyDisbandResponsePacket.PartyDisbandStatus.SUCCESS);
        }

    }

    private void respond(UUID leader, List<UUID> members, PartyDisbandResponsePacket.PartyDisbandStatus partyDisbandStatus) {
        packetUtil.send(new PartyDisbandResponsePacket(leader, members, partyDisbandStatus));
    }

}
