package net.sunken.bungeecord.chat;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.bungeecord.chat.packet.StaffMessagePacket;
import net.sunken.bungeecord.player.BungeePlayer;
import net.sunken.bungeecord.util.ColourUtil;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.player.PlayerDetail;
import net.sunken.common.player.module.PlayerManager;

public class StaffMessageHandler extends PacketHandler<StaffMessagePacket> implements Facet, Enableable {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;

    @Override
    public void onReceive(StaffMessagePacket packet) {
        PlayerDetail sender = packet.getSender();

        playerManager.getOnlinePlayers().stream()
                .filter(player -> player.getRank().has(packet.getTarget()))
                .filter(player -> ((BungeePlayer) player).toProxiedPlayer().isPresent())
                .forEach(player -> {
                    BungeePlayer bungeePlayer = (BungeePlayer) player;
                    ProxiedPlayer proxiedPlayer = bungeePlayer.toProxiedPlayer().get();

                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_AQUA + "[" + ColourUtil.fromColourCode(sender.getRank().getColourCode()) + sender.getDisplayName() + ChatColor.DARK_AQUA + "] " + ChatColor.AQUA + packet.getMessage()));
                });
    }

    @Override
    public void enable() {
        packetHandlerRegistry.registerHandler(StaffMessagePacket.class, this);
    }

    @Override
    public void disable() {
    }

}
