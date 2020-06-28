package net.sunken.common.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bson.Document;

@UtilityClass
public class MongoUtil {

    public static String getStringOrDefault(@NonNull Document document, @NonNull String key, @NonNull String defaultValue) {
        return document.containsKey(key) ? document.getString(key) : defaultValue;
    }

    public static <T extends Enum<T>> Enum<T> getEnumOrDefault(@NonNull Document document, Class<T> enumType, @NonNull String key, @NonNull Enum<T> defaultValue) {
        return document.containsKey(key) ? Enum.valueOf(enumType, document.getString(key)) : defaultValue;
    }
}
