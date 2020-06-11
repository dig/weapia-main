package net.sunken.master.party.handler;

import com.google.inject.Inject;
import net.sunken.common.network.NetworkManager;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.party.packet.PartySetLeaderPacket;
import net.sunken.common.party.packet.PartySetLeaderResponsePacket;
import net.sunken.common.player.PlayerDetail;
import net.sunken.master.party.Party;
import net.sunken.master.party.PartyManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartySetLeaderHandler extends PacketHandler<PartySetLeaderPacket> {

    @Inject
    private PartyManager partyManager;
    @Inject
    private NetworkManager networkManager;
    @Inject
    private PacketUtil packetUtil;

    @Override
    public void onReceive(PartySetLeaderPacket packet) {
        //--- In party?
        Optional<Party> partyOptional = partyManager.findPartyByMember(packet.getUuid());
        if (!partyOptional.isPresent()) {
            respond(packet.getUuid(), packet.getTarget(),null, null, PartySetLeaderResponsePacket.Status.NOT_IN_PARTY);
            return;
        }

        Party party = partyOptional.get();

        //--- Permission
        Optional<PlayerDetail> leaderOptional = party.getLeader();
        if (leaderOptional.isPresent()) {
            PlayerDetail leader = leaderOptional.get();

            if (!leader.getUuid().equals(packet.getUuid())) {
                respond(packet.getUuid(), packet.getTarget(), null, null, PartySetLeaderResponsePacket.Status.NO_PERMISSION);
                return;
            }
        }

        //--- Find target
        Optional<PlayerDetail> targetOptional = networkManager.get(packet.getTarget());
        if (!targetOptional.isPresent()) {
            respond(packet.getUuid(), packet.getTarget(), null, null, PartySetLeaderResponsePacket.Status.TARGET_NOT_FOUND);
            return;
        }

        PlayerDetail targetDetail = targetOptional.get();

        //--- Target is self
        if (packet.getUuid().equals(targetDetail.getUuid())) {
            respond(packet.getUuid(), packet.getTarget(), null, null, PartySetLeaderResponsePacket.Status.TARGET_SELF);
            return;
        }

        //--- Target in party?
        Optional<Party> targetPartyOptional = partyManager.findPartyByMember(targetDetail.getUuid());
        if (targetPartyOptional.isPresent()) {
            Party targetParty = targetPartyOptional.get();

            if (!targetParty.getUuid().equals(party.getUuid())) {
                respond(packet.getUuid(), packet.getTarget(), null, null, PartySetLeaderResponsePacket.Status.TARGET_NOT_IN_PARTY);
                return;
            }
        } else {
            respond(packet.getUuid(), packet.getTarget(), null, null, PartySetLeaderResponsePacket.Status.TARGET_NOT_IN_PARTY);
            return;
        }

        //--- Change leadership
        partyManager.setLeader(party, targetDetail.getUuid());
        respond(packet.getUuid(), packet.getTarget(), targetDetail, party.getMembersAsUuid(), PartySetLeaderResponsePacket.Status.SUCCESS);
    }

    private void respond(UUID player, String target, PlayerDetail newLeader, List<UUID> members, PartySetLeaderResponsePacket.Status status) {
        packetUtil.send(new PartySetLeaderResponsePacket(player, target, newLeader, members, status));
    }

}
