package net.sunken.core.item.impl;

import lombok.Data;
import net.sunken.core.util.NBTItemExact;
import org.bukkit.inventory.ItemStack;

@Data
public class AnItemInstance {

    private final AnItem item;
    private final AnItemAttributes attributes;
    private final ItemStack itemStack;

    public void save() {
        NBTItemExact nbtItem = new NBTItemExact(itemStack);
        attributes.getKeys().forEach(key -> {
            Object value = attributes.get(key);
            nbtItem.setObject(key, value);
        });
    }

}
