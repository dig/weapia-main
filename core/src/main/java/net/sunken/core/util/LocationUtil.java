package net.sunken.core.util;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

public class LocationUtil {

    private static Random random = new Random();

    public static Location findRandomLocation(Location center, int min, int max) {
        Location finalResult = center.clone();
        finalResult.setX(center.getX() + (random.nextInt(max - min) + min));
        finalResult.setZ(center.getZ() + (random.nextInt(max - min) + min));
        return finalResult;
    }

    public static Location findRandomSafeLocation(Location center, int min, int max) {
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
