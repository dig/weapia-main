package net.sunken.bungeecord.party.handler;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.player.BungeePlayer;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.party.packet.PartyCreateResponsePacket;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;

import java.util.Optional;

public class PartyCreateResponseHandler extends PacketHandler<PartyCreateResponsePacket> {

    @Inject
    private PlayerManager playerManager;

    @Override
    public void onReceive(PartyCreateResponsePacket packet) {
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(packet.getUuid());

        if (abstractPlayerOptional.isPresent()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
            Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

            if (proxiedPlayerOptional.isPresent()) {
                ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();

                switch (packet.getPartyCreateStatus()) {
                    case ALREADY_IN_PARTY:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_ALREADY));
                        break;
                    case SUCCESS:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_CREATED));
                        break;
                }
            }
        }
    }

}
