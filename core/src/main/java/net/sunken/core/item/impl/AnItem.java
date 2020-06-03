package net.sunken.core.item.impl;

import lombok.*;
import net.sunken.core.Constants;
import net.sunken.core.inventory.ItemBuilder;
import org.bukkit.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

import java.util.*;

public abstract class AnItem {

    @Getter
    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();
    private final ItemBuilder itemBuilder;

    public AnItem(String id, Material material) {
        this.id = id;
        this.itemBuilder = new ItemBuilder(material)
            .addNBTString(Constants.ITEM_NBT_KEY, this.id);
    }

    public AnItem(String id, ItemBuilder itemBuilder) {
        this.id = id;
        itemBuilder = itemBuilder.addNBTString(Constants.ITEM_NBT_KEY, this.id);
        this.itemBuilder = itemBuilder;
    }

    public AnItem(String id, ItemStack itemStack) {
        this.id = id;
        this.itemBuilder = new ItemBuilder(itemStack)
            .addNBTString(Constants.ITEM_NBT_KEY, this.id);
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String getAttributeAsString(String key) {
        Object attribute = attributes.get(key);
        String stringAttribute = (String) attribute;
        return stringAttribute;
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

    // events
    public void onInventoryClick(InventoryClickEvent event) {}

}
