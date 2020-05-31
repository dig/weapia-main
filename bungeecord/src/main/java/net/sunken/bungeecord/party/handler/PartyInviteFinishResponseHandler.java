package net.sunken.bungeecord.party.handler;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.player.BungeePlayer;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.party.packet.PartyInviteFinishResponsePacket;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;

import java.util.Optional;
import java.util.UUID;

public class PartyInviteFinishResponseHandler extends PacketHandler<PartyInviteFinishResponsePacket> {

    @Inject
    private PlayerManager playerManager;

    @Override
    public void onReceive(PartyInviteFinishResponsePacket packet) {
        //--- Player
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(packet.getPlayer().getUuid());
        if (abstractPlayerOptional.isPresent()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
            Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

            if (proxiedPlayerOptional.isPresent()) {
                ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();

                switch (packet.getState()) {
                    case ALREADY_IN_PARTY:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_ALREADY));
                        break;
                    case NO_INVITE:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_NO_INVITE));
                        break;
                    case DENY:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_DENIED));
                        break;
                    case SUCCESS:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_JOINED));
                        break;
                }
            }
        }

        //--- Leader
        if (packet.getState() == PartyInviteFinishResponsePacket.State.DENY && packet.getPartyLeader() != null) {
            Optional<AbstractPlayer> targetPlayerOptional = playerManager.get(packet.getPartyLeader());

            if (targetPlayerOptional.isPresent()) {
                BungeePlayer bungeePlayer = (BungeePlayer) targetPlayerOptional.get();
                Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

                if (proxiedPlayerOptional.isPresent()) {
                    ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_INVITE_DENY, packet.getPlayer().getDisplayName())));
                }
            }
        }

        //--- Party members
        if (packet.getState() == PartyInviteFinishResponsePacket.State.SUCCESS && packet.getPartyMembers() != null) {
            for (UUID target : packet.getPartyMembers()) {
                Optional<AbstractPlayer> targetPlayerOptional = playerManager.get(target);

                if (targetPlayerOptional.isPresent()) {
                    BungeePlayer bungeePlayer = (BungeePlayer) targetPlayerOptional.get();
                    Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

                    if (proxiedPlayerOptional.isPresent()) {
                        ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_JOINED_OTHER, packet.getPlayer().getDisplayName())));
                    }
                }
            }
        }
    }

}
