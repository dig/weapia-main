package net.sunken.master.party.handler;

import com.google.inject.Inject;
import net.sunken.common.network.NetworkManager;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.party.packet.PartyKickPacket;
import net.sunken.common.party.packet.PartyKickResponsePacket;
import net.sunken.common.player.PlayerDetail;
import net.sunken.master.party.Party;
import net.sunken.master.party.PartyManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyKickHandler extends PacketHandler<PartyKickPacket> {

    @Inject
    private PartyManager partyManager;
    @Inject
    private NetworkManager networkManager;
    @Inject
    private PacketUtil packetUtil;

    @Override
    public void onReceive(PartyKickPacket packet) {
        //--- In party?
        Optional<Party> partyOptional = partyManager.findPartyByMember(packet.getInstigator());
        if (!partyOptional.isPresent()) {
            respond(packet.getInstigator(), packet.getTarget(), null, PartyKickResponsePacket.Status.NOT_IN_PARTY);
            return;
        }

        Party party = partyOptional.get();

        //--- Permission
        Optional<PlayerDetail> leaderOptional = party.getLeader();
        if (leaderOptional.isPresent()) {
            PlayerDetail leader = leaderOptional.get();

            if (!leader.getUuid().equals(packet.getInstigator())) {
                respond(packet.getInstigator(), packet.getTarget(), null, PartyKickResponsePacket.Status.NO_PERMISSION);
                return;
            }
        }

        //--- Find target
        Optional<PlayerDetail> targetOptional = networkManager.get(packet.getTarget());
        if (!targetOptional.isPresent()) {
            respond(packet.getInstigator(), packet.getTarget(), null, PartyKickResponsePacket.Status.TARGET_NOT_FOUND);
            return;
        }

        PlayerDetail targetDetail = targetOptional.get();

        //--- Target is self
        if (packet.getInstigator().equals(targetDetail.getUuid())) {
            respond(packet.getInstigator(), packet.getTarget(), null, PartyKickResponsePacket.Status.TARGET_SELF);
            return;
        }

        //--- Target in party?
        Optional<Party> targetPartyOptional = partyManager.findPartyByMember(targetDetail.getUuid());
        if (targetPartyOptional.isPresent()) {
            Party targetParty = targetPartyOptional.get();

            if (!targetParty.getUuid().equals(party.getUuid())) {
                respond(packet.getInstigator(), packet.getTarget(), null, PartyKickResponsePacket.Status.TARGET_NOT_IN_PARTY);
                return;
            }
        } else {
            respond(packet.getInstigator(), packet.getTarget(), null, PartyKickResponsePacket.Status.TARGET_NOT_IN_PARTY);
            return;
        }

        //--- Kick
        partyManager.leave(party, targetDetail.getUuid());
        respond(packet.getInstigator(), packet.getTarget(), party.getMembersAsUuid(), PartyKickResponsePacket.Status.SUCCESS);
    }

    private void respond(UUID player, String target, List<UUID> members, PartyKickResponsePacket.Status status) {
        packetUtil.send(new PartyKickResponsePacket(player, target, members, status));
    }

}
