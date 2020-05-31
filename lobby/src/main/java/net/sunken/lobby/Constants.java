package net.sunken.lobby;

import net.sunken.core.inventory.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final ItemBuilder ITEM_GAME_SELECTOR = new ItemBuilder(Material.COMPASS)
            .name(ChatColor.GREEN + "Game Selector")
            .lore(ChatColor.WHITE + "Select a gamemode to play.");

    public static final ItemBuilder ITEM_LOBBY_SELECTOR = new ItemBuilder(Material.BOOK)
            .name(ChatColor.YELLOW + "Lobby Selector")
            .lore(ChatColor.WHITE + "Select a lobby to join.");

}
