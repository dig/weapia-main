package net.sunken.master.party.handler;

import com.google.inject.Inject;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.party.packet.PartyInviteFinishPacket;
import net.sunken.common.party.packet.PartyInviteFinishResponsePacket;
import net.sunken.common.player.PlayerDetail;
import net.sunken.master.network.NetworkManager;
import net.sunken.master.party.Party;
import net.sunken.master.party.PartyManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyInviteFinishHandler extends PacketHandler<PartyInviteFinishPacket> {

    @Inject
    private PartyManager partyManager;
    @Inject
    private NetworkManager networkManager;
    @Inject
    private PacketUtil packetUtil;

    @Override
    public void onReceive(PartyInviteFinishPacket packet) {
        Optional<PlayerDetail> playerOptional = networkManager.get(packet.getUuid());
        if (!playerOptional.isPresent()) return;
        PlayerDetail player = playerOptional.get();

        //--- In party?
        if (partyManager.findPartyByMember(packet.getUuid()).isPresent()) {
            respond(player, null, null, PartyInviteFinishResponsePacket.State.ALREADY_IN_PARTY);
            return;
        }

        //--- Is invited?
        Optional<Party> invitedPartyOptional = partyManager.getInvite(packet.getUuid(), packet.getTarget());
        if (!invitedPartyOptional.isPresent()) {
            respond(player, null, null, PartyInviteFinishResponsePacket.State.NO_INVITE);
            return;
        }

        Party invitedParty = invitedPartyOptional.get();

        //--- Attempt to join
        if (packet.getState() == PartyInviteFinishPacket.State.ACCEPT) {
            partyManager.accept(invitedParty, player);
            respond(player, invitedParty.getLeaderUUID(), invitedParty.getMembersAsUuid(), PartyInviteFinishResponsePacket.State.SUCCESS);
        } else {
            partyManager.deny(invitedParty, packet.getUuid());
            respond(player, invitedParty.getLeaderUUID(), null, PartyInviteFinishResponsePacket.State.DENY);
        }
    }

    private void respond(PlayerDetail playerDetail, UUID leader, List<UUID> members, PartyInviteFinishResponsePacket.State state) {
        packetUtil.send(new PartyInviteFinishResponsePacket(playerDetail, leader, members, state));
    }

}
