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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemListener implements Facet, Listener {

    @Inject
    private ItemRegistry itemRegistry;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked != null && clicked.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(clicked);
            if (nbtItem.getKeys().contains(Constants.ITEM_NBT_KEY)) {
                String configName = nbtItem.getString(Constants.ITEM_NBT_KEY);
                Optional<AnItem> anItemOptional = itemRegistry.getItem(configName);
                if (anItemOptional.isPresent()) {
                    AnItem anItem = anItemOptional.get();
                    if (anItem.getListener() != null) {
                        anItem.getListener().onInventoryClick(event);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack clicked = event.getItem();
        if (clicked != null && clicked.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(clicked);
            if (nbtItem.getKeys().contains(Constants.ITEM_NBT_KEY)) {
                String configName = nbtItem.getString(Constants.ITEM_NBT_KEY);
                Optional<AnItem> anItemOptional = itemRegistry.getItem(configName);
                if (anItemOptional.isPresent()) {
                    AnItem anItem = anItemOptional.get();
                    if (anItem.getListener() != null) {
                        anItem.getListener().onInteract(event);
                    }
                }
            }
        }
    }

}
