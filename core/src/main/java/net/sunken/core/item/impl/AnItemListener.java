package net.sunken.core.item.impl;

import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public abstract class AnItemListener {

    public void onInventoryClick(AnItem anItem, InventoryClickEvent event) {}
    public void onInteract(AnItem anItem, PlayerInteractEvent event) {}
    public void onShootBow(AnItem anItem, EntityShootBowEvent event) {}

    public void onDurabilityDamage(AnItem anItem, PlayerItemDamageEvent event) {}

}
