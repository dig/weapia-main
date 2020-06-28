package net.sunken.bungeecord.party.handler;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.player.BungeePlayer;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.party.packet.PartySetLeaderResponsePacket;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.PlayerManager;

import java.util.Optional;
import java.util.UUID;

public class PartySetLeaderResponseHandler extends PacketHandler<PartySetLeaderResponsePacket> {

    @Inject
    private PlayerManager playerManager;

    @Override
    public void onReceive(PartySetLeaderResponsePacket packet) {
        //--- Player
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(packet.getPlayer());
        if (abstractPlayerOptional.isPresent()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
            Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

            if (proxiedPlayerOptional.isPresent()) {
                ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();

                switch (packet.getStatus()) {
                    case NOT_IN_PARTY:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_NONE));
                        break;
                    case NO_PERMISSION:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_LEADER_NO_PERMISSION));
                        break;
                    case TARGET_NOT_FOUND:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_NO_TARGET_FOUND, packet.getTarget())));
                        break;
                    case TARGET_SELF:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_LEADER_SELF));
                        break;
                    case TARGET_NOT_IN_PARTY:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_TARGET_NOT_IN_PARTY, packet.getTarget())));
                        break;
                }
            }
        }

        //--- Members
        if (packet.getStatus() == PartySetLeaderResponsePacket.Status.SUCCESS && packet.getMembers() != null) {
            for (UUID target : packet.getMembers()) {
                Optional<AbstractPlayer> targetPlayerOptional = playerManager.get(target);

                if (targetPlayerOptional.isPresent()) {
                    BungeePlayer bungeePlayer = (BungeePlayer) targetPlayerOptional.get();
                    Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

                    if (proxiedPlayerOptional.isPresent()) {
                        ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_LEADER, packet.getLeader().getDisplayName())));
                    }
                }
            }
        }
    }

}
