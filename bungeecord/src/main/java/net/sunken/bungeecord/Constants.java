package net.sunken.bungeecord;

import net.md_5.bungee.api.ChatColor;
import net.sunken.common.util.StringUtil;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String PLAYER_SEND_SERVER = ChatColor.GREEN + "You have been sent to %s.";

    public static final String DEFAULT_PING_TOP_LINE = "&b&lWeapia &8➤ &6&lPlay Soon! &81.8-1.15";
    public static final String DEFAULT_PING_BOTTOM_LINE = "&fHome of unique minigames - &cComing soon.";

    public static final String FAILED_LOAD_DATA = ChatColor.RED + "Failed to load data for your profile.";
    public static final String FAILED_FIND_SERVER = ChatColor.RED + "Failed to find a server for you to connect to.";
    public static final String FAILED_SERVER_CONNECT = ChatColor.RED + "Only master can connect players to servers.";

    public static final String PROXY_RESTART = ChatColor.RED + "This proxy is restarting in 30 seconds, please connect back to: play.weapia.com.";

    public static final List<String> PLAYER_WELCOME_MESSAGE = Arrays.asList(
            " ",
            StringUtil.center(ChatColor.GREEN + "" + ChatColor.AQUA + "Weapia", 154),
            " ",
            StringUtil.center(ChatColor.GOLD + "Explore the lobby and choose a game to begin:", 154),
            " "
    );

    public static final String MOTD_SUCCESS_CHANGE = ChatColor.GREEN + "Requested MOTD update to all bungeecords.";
    public static final String MOTD_TRUE_FALSE_CENTERED = ChatColor.RED + "true/false are the only accepted values.";

    public static final String CHAT_CANNOT_SEND_AGAIN = ChatColor.RED + "You cannot say the same message twice.";

    public static final String PARTY_ACCEPT_BUTTON = "[ACCEPT]";
    public static final String PARTY_DENY_BUTTON = "[DENY]";
    public static final String PARTY_ALREADY = ChatColor.RED + "You are already in a party.";
    public static final String PARTY_NONE = ChatColor.RED + "You are not in a party.";
    public static final String PARTY_CREATED = ChatColor.AQUA + "Party has been successfully created, invite your friends using /party invite <username>.";
    public static final String PARTY_INVITE_NO_PERMISSION = ChatColor.RED + "You do not have permission to invite players to the party.";
    public static final String PARTY_DISBAND_NO_PERMISSION = ChatColor.RED + "You do not have permission to disband the party.";
    public static final String PARTY_LEADER_NO_PERMISSION = ChatColor.RED + "You do not have permission to change the leader.";
    public static final String PARTY_KICK_NO_PERMISSION = ChatColor.RED + "You do not have permission to kick players.";
    public static final String PARTY_NO_TARGET_FOUND = ChatColor.RED + "Player %s could not be found. Make sure they are online.";
    public static final String PARTY_TARGET_IN_PARTY = ChatColor.RED + "Player %s is already in a party.";
    public static final String PARTY_INVITED_TARGET = ChatColor.AQUA + "You have invited %s to the party, they need to accept in order to join.";
    public static final String PARTY_TARGET_ALREADY_INVITED = ChatColor.RED + "Player %s already has a pending party invitation from you.";
    public static final String PARTY_RECEIVE_INVITE = ChatColor.AQUA + "You have received a party invite from %s.";
    public static final String PARTY_DISBAND = ChatColor.RED + "Party has been disbanded by the leader.";
    public static final String PARTY_NO_INVITE = ChatColor.RED + "You have no party invites from that player.";
    public static final String PARTY_JOINED = ChatColor.AQUA + "You have joined the party!";
    public static final String PARTY_JOINED_OTHER = ChatColor.AQUA + "%s has joined the party!";
    public static final String PARTY_INVITE_DENY = ChatColor.RED + "%s has denied your party invite.";
    public static final String PARTY_DENIED = ChatColor.RED + "You have denied the party invite.";
    public static final String PARTY_CHAT_FORMAT = ChatColor.LIGHT_PURPLE + "Party %s" + ChatColor.WHITE + ": %s";
    public static final String PARTY_LEAVE = ChatColor.AQUA + "You have left the party.";
    public static final String PARTY_LEAVE_OTHER = ChatColor.AQUA + "%s has left the party.";
    public static final String PARTY_IS_LEADER = ChatColor.RED + "You cannot leave the party as the leader, either disband or set leader to another member.";
    public static final String PARTY_TARGET_NOT_IN_PARTY = ChatColor.RED + "Player %s is not in your party.";
    public static final String PARTY_LEADER = ChatColor.AQUA + "%s is now the party leader.";
    public static final String PARTY_INVITE_SELF = ChatColor.RED + "You cannot invite yourself.";
    public static final String PARTY_LEADER_SELF = ChatColor.RED + "You are already leader.";
    public static final String PARTY_KICK_SELF = ChatColor.RED + "You cannot kick yourself.";
    public static final String PARTY_KICK = ChatColor.RED + "%s has been kicked from the party.";

    public static final String COMMAND_SERVER_INVALID = ChatColor.RED + "Invalid game supplied.";
    public static final String COMMAND_SEND_TO_GAME = ChatColor.YELLOW + "Finding a server for %s...";
    public static final String COMMAND_ALREADY_IN_LOBBY = ChatColor.RED + "You are already in a lobby.";

    public static final String COMMAND_CREATESERVER_INVALID = ChatColor.RED + "Invalid type/game supplied.";
    public static final String COMMAND_CREATESERVER_SUCCESS = ChatColor.GREEN + "Requested server to be made.";

}
