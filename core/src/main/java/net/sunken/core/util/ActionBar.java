package net.sunken.core.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

@UtilityClass
public class ActionBar {

    public static void sendMessage(Player bukkitPlayer, String message) {
        sendRawMessage(bukkitPlayer, "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
    }

    public static void sendRawMessage(Player bukkitPlayer, String rawMessage) {
        CraftPlayer player = (CraftPlayer) bukkitPlayer;
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a(rawMessage);
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chatBaseComponent, ChatMessageType.GAME_INFO);
        player.getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }
}
