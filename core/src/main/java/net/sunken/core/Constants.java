package net.sunken.core;

import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Constants {

    public static final String FAILED_LOAD_DATA = ChatColor.RED + "Failed to load data for your profile.";

    public static final String TAB_TOP = "\n" + ChatColor.AQUA + "" + ChatColor.BOLD + "WEAPIA\n";
    public static final String TAB_BOTTOM = "\n" + ChatColor.WHITE + "Join the VIP Club\n    and get glossy perks    \n" + ChatColor.DARK_AQUA + "/join\n";

    public static final String NETWORKCOMMAND_COOLDOWN = ChatColor.RED + "Please wait before trying this command again.";

    public static final List<String> WHITELISTED_DEFAULT_COMMANDS = Arrays.asList(
            "packet_filter", "filter", "protocol", "packetlog", "packet", "shop", "dm", "deluxemenu",
            "mmoxpbar", "mmoinfo", "xprate", "mcmmo", "mctop", "mcrank", "addxp", "addlevels", "mcability", "mcrefresh", "mccooldown", "mcchatspy", "mcgod", "mcimport", "mcstats", "mcremove", "mmoedit", "inspect", "skillreset", "excavation", "herbalism", "mining", "woodcutting", "axes", "archery", "swords", "taming", "unarmed", "acrobatics", "repair", "fishing", "smelting", "alchemy", "salvage", "mcpurge", "hardcore", "vampirism", "mcnotify", "mhd", "mcmmoreloadlocale"
    );

    public static final String SEND_TO_GAME = ChatColor.YELLOW + "Finding a server for %s...";

    public static final String COUNTDOWN_LOBBY_SECONDS = ChatColor.YELLOW + "Game starting in %ds";
    public static final String COUNTDOWN_STARTING = ChatColor.YELLOW + "Game now starting.";
    public static final String COUNTDOWN_FAILED_REQUIRED = ChatColor.YELLOW + "Waiting for more players to join...";
    public static final String COUNTDOWN_FAILED_STATE_NULL = ChatColor.RED + "Appears this minigame is not setup correctly, please contact administrators with this message.";

    public static final String ELEMENT_NBT_KEY = "WeapiaElement";
    public static final String ITEM_NBT_KEY = "WeapiaItem";
    public static final String ITEM_UUID_NBT_KEY = "WeapiaItemUUID";

    public static final String COMMAND_WHERE = ChatColor.GREEN + "You are on server %s running %s, please include this in any reports you make.";

    public static final String COMMAND_ITEM_ID_INVALID = ChatColor.RED + "Invalid item.";
    public static final String COMMAND_ITEM_SUCCESS = ChatColor.GREEN + "Added item.";

    public static final String COMMAND_ITEM_TARGET_INVALID = ChatColor.RED + "Invalid target player.";
    public static final String COMMAND_GIVE_ITEM_SUCCESS = ChatColor.GREEN + "Added item to target player.";

    public static final String COMMAND_NAMETAG_SUCCESS = ChatColor.GREEN + "Changed nametag.";

    public static final String HOLOGRAM_METADATA_KEY = "WeapiaHologram";

}
