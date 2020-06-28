package net.sunken.bungeecord.party.handler;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.player.BungeePlayer;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.party.packet.PartyDisbandResponsePacket;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.PlayerManager;

import java.util.Optional;
import java.util.UUID;

public class PartyDisbandResponseHandler extends PacketHandler<PartyDisbandResponsePacket> {

    @Inject
    private PlayerManager playerManager;

    @Override
    public void onReceive(PartyDisbandResponsePacket packet) {
        //--- Leader
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(packet.getLeader());
        if (abstractPlayerOptional.isPresent()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
            Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

            if (proxiedPlayerOptional.isPresent()) {
                ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();

                switch (packet.getPartyDisbandStatus()) {
                    case NOT_IN_PARTY:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_NONE));
                        break;
                    case NO_PERMISSION:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_DISBAND_NO_PERMISSION));
                        break;
                }
            }
        }

        //--- Members
        if (packet.getPartyDisbandStatus() == PartyDisbandResponsePacket.PartyDisbandStatus.SUCCESS && packet.getMembers() != null && packet.getMembers().size() > 0) {
            for (UUID uuid : packet.getMembers()) {
                Optional<AbstractPlayer> targetPlayerOptional = playerManager.get(uuid);

                if (targetPlayerOptional.isPresent()) {
                    BungeePlayer bungeePlayer = (BungeePlayer) targetPlayerOptional.get();
                    Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

                    if (proxiedPlayerOptional.isPresent()) {
                        ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_DISBAND));
                    }
                }
            }
        }
    }

}
