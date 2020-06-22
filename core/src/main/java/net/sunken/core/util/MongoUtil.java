package net.sunken.core.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.sunken.common.database.DatabaseHelper;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@UtilityClass
public class MongoUtil {

    public static String getStringOrDefault(@NonNull Document document, @NonNull String key, @NonNull String defaultValue) {
        return document.containsKey(key) ? document.getString(key) : defaultValue;
    }

    public static <T extends Enum<T>> Enum<T> getEnumOrDefault(@NonNull Document document, Class<T> enumType, @NonNull String key, @NonNull Enum<T> defaultValue) {
        return document.containsKey(key) ? Enum.valueOf(enumType, document.getString(key)) : defaultValue;
    }

    public static Location location(@NonNull Document document, boolean containWorld) {
        return new Location(
                containWorld ? Bukkit.getWorld(document.getString(DatabaseHelper.LOCATION_WORLD_KEY)) : null,
                document.getDouble(DatabaseHelper.LOCATION_X_KEY),
                document.getDouble(DatabaseHelper.LOCATION_Y_KEY),
                document.getDouble(DatabaseHelper.LOCATION_Z_KEY),
                document.getDouble(DatabaseHelper.LOCATION_YAW_KEY).floatValue(),
                document.getDouble(DatabaseHelper.LOCATION_PITCH_KEY).floatValue());
    }

    public static Document location(@NonNull Location location, boolean containWorld) {
        Document document = new Document()
                .append(DatabaseHelper.LOCATION_X_KEY, location.getX())
                .append(DatabaseHelper.LOCATION_Y_KEY, location.getY())
                .append(DatabaseHelper.LOCATION_Z_KEY, location.getZ())
                .append(DatabaseHelper.LOCATION_YAW_KEY, location.getYaw())
                .append(DatabaseHelper.LOCATION_PITCH_KEY, location.getPitch());

        if (containWorld) document.append(DatabaseHelper.LOCATION_WORLD_KEY, location.getWorld().getName());
        return document;
    }
}
