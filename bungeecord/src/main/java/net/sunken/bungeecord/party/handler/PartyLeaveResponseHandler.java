package net.sunken.bungeecord.party.handler;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.player.BungeePlayer;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.party.packet.PartyLeaveResponsePacket;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;

import java.util.Optional;
import java.util.UUID;

public class PartyLeaveResponseHandler extends PacketHandler<PartyLeaveResponsePacket> {

    @Inject
    private PlayerManager playerManager;

    @Override
    public void onReceive(PartyLeaveResponsePacket packet) {
        //--- Leaver
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(packet.getPlayer().getUuid());
        if (abstractPlayerOptional.isPresent()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
            Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

            if (proxiedPlayerOptional.isPresent()) {
                ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();

                switch (packet.getStatus()) {
                    case NOT_IN_PARTY:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_NONE));
                        break;
                    case LEADER:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_IS_LEADER));
                        break;
                    case SUCCESS:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_LEAVE));
                        break;
                }
            }
        }

        //--- Targets
        if (packet.getStatus() == PartyLeaveResponsePacket.Status.SUCCESS && packet.getMembers() != null) {
            for (UUID target : packet.getMembers()) {
                Optional<AbstractPlayer> targetPlayerOptional = playerManager.get(target);

                if (targetPlayerOptional.isPresent()) {
                    BungeePlayer bungeePlayer = (BungeePlayer) targetPlayerOptional.get();
                    Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

                    if (proxiedPlayerOptional.isPresent()) {
                        ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_LEAVE_OTHER, packet.getPlayer().getDisplayName())));
                    }
                }
            }
        }
    }

}
