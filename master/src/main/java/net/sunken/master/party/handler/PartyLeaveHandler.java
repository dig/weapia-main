package net.sunken.master.party.handler;

import com.google.inject.Inject;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.party.packet.PartyLeavePacket;
import net.sunken.common.party.packet.PartyLeaveResponsePacket;
import net.sunken.common.player.PlayerDetail;
import net.sunken.master.party.Party;
import net.sunken.master.party.PartyManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyLeaveHandler extends PacketHandler<PartyLeavePacket> {

    @Inject
    private PartyManager partyManager;
    @Inject
    private PacketUtil packetUtil;

    @Override
    public void onReceive(PartyLeavePacket packet) {
        //--- In party?
        Optional<Party> partyOptional = partyManager.findPartyByMember(packet.getPlayer().getUuid());
        if (!partyOptional.isPresent()) {
            respond(packet.getPlayer(), null, PartyLeaveResponsePacket.Status.NOT_IN_PARTY);
            return;
        }

        Party party = partyOptional.get();

        //--- Not leader
        Optional<PlayerDetail> leaderOptional = party.getLeader();
        if (leaderOptional.isPresent()) {
            PlayerDetail leader = leaderOptional.get();

            if (leader.getUuid().equals(packet.getPlayer().getUuid())) {
                respond(packet.getPlayer(), null, PartyLeaveResponsePacket.Status.LEADER);
                return;
            }
        }

        partyManager.leave(party, packet.getPlayer().getUuid());
        respond(packet.getPlayer(), party.getMembersAsUuid(), PartyLeaveResponsePacket.Status.SUCCESS);
    }

    private void respond(PlayerDetail player, List<UUID> members, PartyLeaveResponsePacket.Status status) {
        packetUtil.send(new PartyLeaveResponsePacket(player, members, status));
    }

}
