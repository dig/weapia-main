package net.sunken.core.item;

import com.google.inject.Inject;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.sunken.common.inject.Facet;
import net.sunken.core.Constants;
import net.sunken.core.item.impl.AnItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemListener implements Facet, Listener {

    @Inject
    private ItemRegistry itemRegistry;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked != null && clicked.getType() != Material.AIR) {
            Optional<AnItem> anItemOptional = itemRegistry.getItem(clicked);
            if (anItemOptional.isPresent()) {
                AnItem anItem = anItemOptional.get();
                if (anItem.getListener() != null) {
                    anItem.getListener().onInventoryClick(anItem, event);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack clicked = event.getItem();
        if (clicked != null && clicked.getType() != Material.AIR) {
            Optional<AnItem> anItemOptional = itemRegistry.getItem(clicked);
            if (anItemOptional.isPresent()) {
                AnItem anItem = anItemOptional.get();
                if (anItem.getListener() != null) {
                    anItem.getListener().onInteract(anItem, event);
                }
            }
        }
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            ItemStack bow = event.getBow();
            if (bow != null && bow.getType() == Material.BOW) {
                Optional<AnItem> anItemOptional = itemRegistry.getItem(bow);
                if (anItemOptional.isPresent()) {
                    AnItem anItem = anItemOptional.get();
                    if (anItem.getListener() != null) {
                        anItem.getListener().onShootBow(anItem, event);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDurabilityDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType() != Material.AIR) {
            Optional<AnItem> anItemOptional = itemRegistry.getItem(item);
            if (anItemOptional.isPresent()) {
                AnItem anItem = anItemOptional.get();
                if (anItem.getListener() != null) {
                    anItem.getListener().onDurabilityDamage(anItem, event);
                }
            }
        }
    }

}
