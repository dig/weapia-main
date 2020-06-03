package net.sunken.core.item.impl;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class AnItemListener {

    public void onInventoryClick(InventoryClickEvent event) {}
    public void onInteract(PlayerInteractEvent event) {}

}
