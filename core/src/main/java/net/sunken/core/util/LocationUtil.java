package net.sunken.core.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

@UtilityClass
public class LocationUtil {

    private static Random random = new Random();

    public static Location findRandomLocation(@NonNull Location center, int min, int max) {
        Location finalResult = center.clone();
        finalResult.setX(center.getX() + (random.nextInt(max - min) + min));
        finalResult.setZ(center.getZ() + (random.nextInt(max - min) + min));
        return finalResult;
    }

    public static Location findRandomSafeLocation(@NonNull Location center, int min, int max) {
        Location finalResult = findRandomLocation(center, min, max);
        finalResult.setY(finalResult.getWorld().getHighestBlockYAt(finalResult) - 1);

        while (finalResult.getBlock().getType() == Material.WATER
                || finalResult.getBlock().getType() == Material.LAVA) {
            finalResult = findRandomLocation(center, min, max);
            finalResult.setY(finalResult.getWorld().getHighestBlockYAt(finalResult) - 1);
        }

        return finalResult;
    }
}
