package net.sunken.bungeecord.party.handler;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.player.BungeePlayer;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.party.packet.PartyInviteResponsePacket;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.PlayerManager;

import java.util.Optional;

public class PartyInviteResponseHandler extends PacketHandler<PartyInviteResponsePacket> {

    @Inject
    private PlayerManager playerManager;

    @Override
    public void onReceive(PartyInviteResponsePacket packet) {
        //--- Inviter
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(packet.getInviter().getUuid());
        if (abstractPlayerOptional.isPresent()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
            Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

            if (proxiedPlayerOptional.isPresent()) {
                ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();

                switch (packet.getPartyInviteStatus()) {
                    case NOT_IN_PARTY:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_NONE));
                        break;
                    case NO_PERMISSION:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_INVITE_NO_PERMISSION));
                        break;
                    case TARGET_NOT_FOUND:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_NO_TARGET_FOUND, packet.getTarget())));
                        break;
                    case TARGET_SELF:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Constants.PARTY_INVITE_SELF));
                        break;
                    case TARGET_ALREADY_INVITED:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_TARGET_ALREADY_INVITED, packet.getTarget())));
                        break;
                    case TARGET_ALREADY_IN_PARTY:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_TARGET_IN_PARTY, packet.getTarget())));
                        break;
                    case SUCCESS:
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_INVITED_TARGET, packet.getTarget())));
                        break;
                }
            }
        }

        //--- Target
        if (packet.getPartyInviteStatus() == PartyInviteResponsePacket.PartyInviteStatus.SUCCESS && packet.getTargetUUID() != null) {
            Optional<AbstractPlayer> targetPlayerOptional = playerManager.get(packet.getTargetUUID());

            if (targetPlayerOptional.isPresent()) {
                BungeePlayer bungeePlayer = (BungeePlayer) targetPlayerOptional.get();
                Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

                if (proxiedPlayerOptional.isPresent()) {
                    ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();

                    TextComponent acceptButton = new TextComponent(Constants.PARTY_ACCEPT_BUTTON);
                    acceptButton.setColor(ChatColor.GREEN);
                    acceptButton.setBold(true);
                    acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/party accept %s", packet.getInviter().getDisplayName())));

                    TextComponent denyButton = new TextComponent(Constants.PARTY_DENY_BUTTON);
                    denyButton.setColor(ChatColor.RED);
                    denyButton.setBold(true);
                    denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/party deny %s", packet.getInviter().getDisplayName())));

                    acceptButton.addExtra(" ");
                    acceptButton.addExtra(denyButton);

                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PARTY_RECEIVE_INVITE, packet.getInviter().getDisplayName())));
                    proxiedPlayer.sendMessage(acceptButton);
                }
            }
        }
    }

}
