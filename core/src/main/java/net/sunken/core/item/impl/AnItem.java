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
    @Getter
    private final Map<String, Object> attributes = new HashMap<>();
    private final ItemBuilder itemBuilder;

    public AnItem(String id, Material material) {
        this.id = id;
        this.itemBuilder = new ItemBuilder(material)
            .addNBTString(Constants.ITEM_NBT_KEY, this.id);
    }

    public AnItem(String id, ItemBuilder itemBuilder) {
        this.id = id;
        this.itemBuilder = itemBuilder;
    }

    public AnItem(String id, ItemStack itemStack) {
        this.id = id;
        this.itemBuilder = new ItemBuilder(itemStack);
    }

    public void onInventoryClick(InventoryClickEvent event) {}

    public ItemStack toItemStack() {
        return itemBuilder.make();
    }

}
