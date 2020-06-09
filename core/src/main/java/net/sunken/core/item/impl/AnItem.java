package net.sunken.core.item.impl;

import lombok.*;
import net.sunken.core.Constants;
import net.sunken.core.inventory.ItemBuilder;
import org.bukkit.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.*;

public class AnItem {

    @Getter
    private final String id;
    @Getter
    private final AnItemAttributes attributes;
    @Getter
    private final AnItemListener listener;
    private final boolean stack;
    private final ItemBuilder itemBuilder;

    public AnItem(@NonNull String id, @NonNull ItemBuilder itemBuilder, AnItemListener listener, boolean stack) {
        this.id = id;
        this.attributes = new AnItemAttributes();
        this.listener = listener;
        this.stack = stack;
        this.itemBuilder = itemBuilder
                .addNBTString(Constants.ITEM_NBT_KEY, this.id);
    }

    public ItemStack toItemStack() {
        ItemBuilder itemBuilder = this.itemBuilder;
        if (!this.stack) {
            itemBuilder = itemBuilder.addNBTString(Constants.ITEM_UUID_NBT_KEY, UUID.randomUUID().toString());
        }

        return itemBuilder.make();
    }

}
