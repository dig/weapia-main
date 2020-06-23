package net.sunken.core.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

@Log
@UtilityClass
public class PotionEffectUtil {

    public static String encode(@NonNull Collection<PotionEffect> potionEffects) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);
            bukkitObjectOutputStream.writeInt(potionEffects.toArray().length);
            for (int i = 0; i < potionEffects.toArray().length; ++i) {
                bukkitObjectOutputStream.writeObject(potionEffects.toArray()[i]);
            }
            bukkitObjectOutputStream.close();
            return new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to create BukkitObjectOutputStream", e);
        }

        return null;
    }

    public static Collection<PotionEffect> decode(@NonNull String value) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(value));
        try {
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
            PotionEffect[] array = new PotionEffect[bukkitObjectInputStream.readInt()];

            List<PotionEffect> list = new ArrayList<>();
            for (int i = 0; i < array.length; ++i) {
                list.add((PotionEffect) bukkitObjectInputStream.readObject());
            }

            bukkitObjectInputStream.close();
            return list;
        } catch (IOException | ClassNotFoundException e) {
            log.log(Level.SEVERE, "Unable to create BukkitObjectOutputStream", e);
        }

        return new ArrayList<>();
    }
}
