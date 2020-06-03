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
    private final Map<String, Object> attributes = new HashMap<>();
    private final ItemBuilder itemBuilder;

    @Getter
    private AnItemListener listener;

    public AnItem(@NonNull String id, @NonNull ItemBuilder itemBuilder, AnItemListener listener, boolean stack) {
        this.id = id;
        this.listener = listener;

        if (!stack) {
            this.itemBuilder = itemBuilder
                    .addNBTString(Constants.ITEM_NBT_KEY, this.id)
                    .addNBTString(Constants.ITEM_UUID_NBT_KEY, UUID.randomUUID().toString());
        } else {
            this.itemBuilder = itemBuilder
                    .addNBTString(Constants.ITEM_NBT_KEY, this.id);
        }
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String getAttributeAsString(String key) {
        Object attribute = attributes.get(key);
        return (String) attribute;
    }

    public int getAttributeAsInt(String key) {
        Object attribute = attributes.get(key);
        return (int) attribute;
    }

    public boolean getAttributeAsBoolean(String key) {
        Object attribute = attributes.get(key);
        return (boolean) attribute;
    }

    public double getAttributeAsDouble(String key) {
        Object attribute = attributes.get(key);
        return (double) attribute;
    }

    public ItemStack toItemStack() {
        return itemBuilder.make();
    }

}
