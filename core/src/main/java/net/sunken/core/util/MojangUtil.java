package net.sunken.core.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.NonNull;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MojangUtil {

    public static GameProfile toGameProfile(@NonNull String displayName, @NonNull String texture, @NonNull String signature) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', displayName));
        profile.getProperties().put("textures", new Property("textures", texture, signature));
        return profile;
    }

    public static String[] getSkinData(@NonNull Player player) {
        EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
        GameProfile profile = playerNMS.getProfile();

        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();

        return new String[] {texture, signature};
    }

}
