package net.sunken.core.util;

import org.bukkit.*;
import org.bukkit.command.*;

import java.lang.reflect.*;
import java.util.*;

public final class CommandUtil {

    public static Optional<SimpleCommandMap> getCommandMap() {
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

    private CommandUtil() {
        throw new UnsupportedOperationException("cannot instantiate");
    }
}
