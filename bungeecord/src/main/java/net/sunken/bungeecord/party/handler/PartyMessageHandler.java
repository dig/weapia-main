package net.sunken.bungeecord.party.handler;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.player.BungeePlayer;
import net.sunken.bungeecord.util.ColourUtil;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.party.packet.PartyMessagePacket;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.PlayerDetail;
import net.sunken.common.player.Rank;
import net.sunken.common.player.module.PlayerManager;

import java.util.Optional;
import java.util.UUID;

public class PartyMessageHandler extends PacketHandler<PartyMessagePacket> {

    @Inject
    private PlayerManager playerManager;

    @Override
    public void onReceive(PartyMessagePacket packet) {
        PlayerDetail sender = packet.getSender();

        String playerFormat = ColourUtil.fromColourCode(sender.getRank().getColourCode()) + (sender.getRank() == Rank.PLAYER ? "" : "[" + sender.getRank().getFriendlyName().toUpperCase() + "] ") + sender.getDisplayName();
        BaseComponent[] message = TextComponent.fromLegacyText(String.format(Constants.PARTY_CHAT_FORMAT, playerFormat, packet.getMessage()));

        for (UUID target : packet.getTargets()) {
            Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(target);

            if (abstractPlayerOptional.isPresent()) {
                BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
                Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

                if (proxiedPlayerOptional.isPresent())
                    proxiedPlayerOptional.get().sendMessage(message);
            }
        }
    }

}
