package net.sunken.core.inventory.element;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.inject.Facet;
import net.sunken.core.Constants;
import net.sunken.core.inventory.runnable.UIRunnableContext;
import net.sunken.core.util.nbt.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Log
public class ElementListener implements Facet, Listener {

    @Inject
    private ElementRegistry elementRegistry;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked != null) {
            NBTItem nbtItem = new NBTItem(clicked);
            if (nbtItem.getKeys().contains(Constants.ELEMENT_NBT_KEY)) {
                String uuid = nbtItem.getString(Constants.ELEMENT_NBT_KEY);
                Element element = elementRegistry.getRegistry().getIfPresent(UUID.fromString(uuid));

                if (element != null) {
                    event.setCancelled(true);

                    if (element instanceof ActionableElement) {
                        ActionableElement actionableElement = (ActionableElement) element;

                        if (actionableElement.getAction() == Action.CLICK || actionableElement.getAction() == Action.BOTH) {
                            UIRunnableContext context = new UIRunnableContext(player, actionableElement.getItem());
                            context.setCancelled(event.isCancelled());

                            UIRunnableContext modifiedContext = ((ActionableElement) element).getRunnable()
                                    .run(context);
                            event.setCancelled(modifiedContext.isCancelled());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack clicked = event.getItem();

        if (clicked != null) {
            NBTItem nbtItem = new NBTItem(clicked);
            if (nbtItem.getKeys().contains(Constants.ELEMENT_NBT_KEY)) {
                String uuid = nbtItem.getString(Constants.ELEMENT_NBT_KEY);
                Element element = elementRegistry.getRegistry().getIfPresent(UUID.fromString(uuid));

                if (element != null) {
                    if (element instanceof ActionableElement) {
                        ActionableElement actionableElement = (ActionableElement) element;

                        if (actionableElement.getAction() == Action.INTERACT || actionableElement.getAction() == Action.BOTH) {
                            UIRunnableContext context = new UIRunnableContext(player, actionableElement.getItem());
                            context.setCancelled(event.isCancelled());

                            UIRunnableContext modifiedContext = ((ActionableElement) element).getRunnable()
                                    .run(context);
                            event.setCancelled(modifiedContext.isCancelled());
                        }
                    }
                }
            }
        }
    }

}
