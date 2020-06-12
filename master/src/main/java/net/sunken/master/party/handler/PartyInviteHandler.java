package net.sunken.master.party.handler;

import com.google.inject.Inject;
import net.sunken.common.network.NetworkManager;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.party.packet.PartyCreateResponsePacket;
import net.sunken.common.party.packet.PartyInvitePacket;
import net.sunken.common.party.packet.PartyInviteResponsePacket;
import net.sunken.common.player.PlayerDetail;
import net.sunken.master.party.Party;
import net.sunken.master.party.PartyManager;

import java.util.Optional;
import java.util.UUID;

public class PartyInviteHandler extends PacketHandler<PartyInvitePacket> {

    @Inject
    private PartyManager partyManager;
    @Inject
    private NetworkManager networkManager;
    @Inject
    private PacketUtil packetUtil;

    @Override
    public void onReceive(PartyInvitePacket packet) {
        Optional<Party> partyOptional = partyManager.findPartyByMember(packet.getInviter().getUuid());

        //--- In party? if not, create.
        if (!partyOptional.isPresent()) {
            PartyCreateResponsePacket.PartyCreateStatus partyCreateStatus = partyManager.createParty(packet.getInviter());
            if (partyCreateStatus == PartyCreateResponsePacket.PartyCreateStatus.SUCCESS) {
                partyOptional = partyManager.findPartyByMember(packet.getInviter().getUuid());
            } else {
                respond(packet.getInviter(), packet.getTarget(), null, PartyInviteResponsePacket.PartyInviteStatus.NOT_IN_PARTY);
                return;
            }
        }

        Party party = partyOptional.get();

        //--- Permission
        if (party.getLeader().isPresent()) {
            PlayerDetail leader = party.getLeader().get();

            if (!leader.getUuid().equals(packet.getInviter().getUuid())) {
                respond(packet.getInviter(), packet.getTarget(), null, PartyInviteResponsePacket.PartyInviteStatus.NO_PERMISSION);
                return;
            }
        }

        //--- Find target
        Optional<PlayerDetail> targetOptional = networkManager.get(packet.getTarget());
        if (!targetOptional.isPresent()) {
            respond(packet.getInviter(), packet.getTarget(), null, PartyInviteResponsePacket.PartyInviteStatus.TARGET_NOT_FOUND);
            return;
        }

        PlayerDetail targetDetail = targetOptional.get();

        //--- Target is self
        if (packet.getInviter().getUuid().equals(targetDetail.getUuid())) {
            respond(packet.getInviter(), packet.getTarget(), null, PartyInviteResponsePacket.PartyInviteStatus.TARGET_SELF);
            return;
        }

        //--- Target already invited?
        if (partyManager.isInvitedToParty(party, targetDetail.getUuid())) {
            respond(packet.getInviter(), packet.getTarget(), null, PartyInviteResponsePacket.PartyInviteStatus.TARGET_ALREADY_INVITED);
            return;
        }

        //--- Target in party?
        Optional<Party> targetPartyOptional = partyManager.findPartyByMember(targetDetail.getUuid());
        if (targetPartyOptional.isPresent()) {
            respond(packet.getInviter(), packet.getTarget(), null, PartyInviteResponsePacket.PartyInviteStatus.TARGET_ALREADY_IN_PARTY);
            return;
        }

        //--- Finally invite to party
        PartyInviteResponsePacket.PartyInviteStatus partyInviteStatus = partyManager.invite(party, targetDetail.getUuid());
        respond(packet.getInviter(), packet.getTarget(), targetDetail.getUuid(), partyInviteStatus);
    }

    private void respond(PlayerDetail inviter, String targetName, UUID targetUUID, PartyInviteResponsePacket.PartyInviteStatus partyInviteStatus) {
        packetUtil.send(new PartyInviteResponsePacket(inviter, targetName, targetUUID, partyInviteStatus));
    }

}
