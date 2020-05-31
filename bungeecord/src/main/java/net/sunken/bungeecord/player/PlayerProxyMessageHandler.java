package net.sunken.bungeecord.player;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.player.packet.PlayerProxyMessagePacket;

import java.util.Optional;

public class PlayerProxyMessageHandler extends PacketHandler<PlayerProxyMessagePacket> {

    @Inject
    private PlayerManager playerManager;

    @Override
    public void onReceive(PlayerProxyMessagePacket packet) {
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(packet.getTarget());

        if (abstractPlayerOptional.isPresent()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
            Optional<ProxiedPlayer> proxiedPlayerOptional = bungeePlayer.toProxiedPlayer();

            if (proxiedPlayerOptional.isPresent()) {
                ProxiedPlayer proxiedPlayer = proxiedPlayerOptional.get();

                proxiedPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', packet.getMessage())));
            }
        }
    }

}
