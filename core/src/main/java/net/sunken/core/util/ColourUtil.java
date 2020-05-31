package net.sunken.core.util;

import lombok.NonNull;
import org.bukkit.ChatColor;

public class ColourUtil {

    public static ChatColor fromColourCode(@NonNull String colourCode) {
        switch (colourCode) {
            case "&4": return ChatColor.DARK_RED;
            case "&c": return ChatColor.RED;
            case "&6": return ChatColor.GOLD;
            case "&e": return ChatColor.YELLOW;
            case "&2": return ChatColor.DARK_GREEN;
            case "&a": return ChatColor.GREEN;
            case "&b": return ChatColor.AQUA;
            case "&3": return ChatColor.DARK_AQUA;
            case "&1": return ChatColor.DARK_BLUE;
            case "&9": return ChatColor.BLUE;
            case "&d": return ChatColor.LIGHT_PURPLE;
            case "&5": return ChatColor.DARK_PURPLE;
            case "&f": return ChatColor.WHITE;
            case "&7": return ChatColor.GRAY;
            case "&8": return ChatColor.DARK_GRAY;
            case "&0": return ChatColor.BLACK;
            default: return null;
        }
    }

}
