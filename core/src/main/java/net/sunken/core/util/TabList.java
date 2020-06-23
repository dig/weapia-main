package net.sunken.core.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

@UtilityClass
public class TabList {

    public static void send(@NonNull Player player, @NonNull String header, @NonNull String footer) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        IChatBaseComponent tabTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
        IChatBaseComponent tabFoot = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
        PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter();

        try {
            Field a = headerPacket.getClass().getDeclaredField("header");
            a.setAccessible(true);
            a.set(headerPacket, tabTitle);

            Field b = headerPacket.getClass().getDeclaredField("footer");
            b.setAccessible(true);
            b.set(headerPacket, tabFoot);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.sendPacket(headerPacket);
        }
    }
}
