package net.sunken.core.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.sunken.common.database.DatabaseHelper;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@UtilityClass
public class MongoUtil {

    public static Location location(@NonNull Document document) {
        return new Location(
                Bukkit.getWorld(document.getString(DatabaseHelper.LOCATION_WORLD_KEY)),
                document.getDouble(DatabaseHelper.LOCATION_X_KEY),
                document.getDouble(DatabaseHelper.LOCATION_Y_KEY),
                document.getDouble(DatabaseHelper.LOCATION_Z_KEY),
                document.getDouble(DatabaseHelper.LOCATION_YAW_KEY).floatValue(),
                document.getDouble(DatabaseHelper.LOCATION_PITCH_KEY).floatValue());
    }

    public static Document location(@NonNull Location location) {
        return new Document()
                .append(DatabaseHelper.LOCATION_WORLD_KEY, location.getWorld().getName())
                .append(DatabaseHelper.LOCATION_X_KEY, location.getX())
                .append(DatabaseHelper.LOCATION_Y_KEY, location.getY())
                .append(DatabaseHelper.LOCATION_Z_KEY, location.getZ())
                .append(DatabaseHelper.LOCATION_YAW_KEY, location.getYaw())
                .append(DatabaseHelper.LOCATION_PITCH_KEY, location.getPitch());
    }
}
