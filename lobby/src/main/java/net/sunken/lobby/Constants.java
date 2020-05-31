package net.sunken.lobby;

import net.sunken.core.inventory.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final ItemBuilder ITEM_GAME_SELECTOR = new ItemBuilder(Material.COMPASS)
            .name(ChatColor.GREEN + " \u2996 Game Selector")
            .lore(ChatColor.WHITE + "Select a gamemode to play.");

    public static final ItemBuilder ITEM_LOBBY_SELECTOR = new ItemBuilder(Material.BOOK)
            .name(ChatColor.YELLOW + " \u2996 Lobby Selector")
            .lore(ChatColor.WHITE + "Select a lobby to join.");

}
