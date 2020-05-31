package net.sunken.core;

import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Constants {

    public static final String FAILED_LOAD_DATA = ChatColor.RED + "Failed to load data for your profile.";

    public static final String TAB_TOP = "\n" + ChatColor.AQUA + ""  + ChatColor.BOLD + "WEAPIA\n";
    public static final String TAB_BOTTOM = "\n" + ChatColor.WHITE + "Join the VIP Club\n    and get glossy perks    \n" + ChatColor.DARK_AQUA + "/view\n";

    public static final String INTERACTION_COOLDOWN = ChatColor.RED + "Please wait before doing that again.";

    public static final List<String> WHITELISTED_DEFAULT_COMMANDS = Arrays.asList("packet_filter", "filter", "protocol", "packetlog", "packet");

    public static final String DEFAULT_BOSS_BAR = "";

    public static final String BAR_SUCCESS_CHANGE = ChatColor.GREEN + "Requested update to all servers.";
    public static final String BAR_CLEARED = ChatColor.RED + "Cleared boss and action bar.";

    public static final String SEND_TO_GAME = ChatColor.YELLOW + "Finding a server for %s...";

    public static final String COUNTDOWN_LOBBY_SECONDS = ChatColor.YELLOW + "Lobby ending in %d second(s).";
    public static final String COUNTDOWN_GAME_SECONDS = ChatColor.YELLOW + "Game starting in %d second(s).";
    public static final String COUNTDOWN_STARTING = ChatColor.YELLOW + "Preparing game.";
    public static final String COUNTDOWN_FAILED_REQUIRED = ChatColor.YELLOW + "Waiting for more players to join...";
    public static final String COUNTDOWN_FAILED_STATE_NULL = ChatColor.RED + "Appears this minigame is not setup correctly, please contact administrators with this message.";

    public static final String GAME_ERROR = ChatColor.RED + "There was an error with the game you were in therefore you have been sent back to the lobby.";

    public static final String ELEMENT_NBT_KEY = "MinevasionElement";

    public static final String COMMAND_WHERE = ChatColor.GREEN + "You are on server %s running %s, please include this in any reports you make.";

    public final static String GAME_PLAY_AGAIN = "[PLAY AGAIN]";

}
