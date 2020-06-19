package net.sunken.core.util;

import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.command.*;

import java.lang.reflect.*;
import java.util.*;

@UtilityClass
public final class CommandUtil {

    public static Optional<SimpleCommandMap> findSimpleCommandMap() {
        try {
            Field bukkitCommandMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) bukkitCommandMap.get(Bukkit.getPluginManager());

            return Optional.of(commandMap);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

}
