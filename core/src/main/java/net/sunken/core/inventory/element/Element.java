package net.sunken.core.inventory.element;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import net.sunken.core.Constants;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Element {

    private ElementRegistry elementRegistry;

    @Getter
    private UUID uuid;

    @Getter
    protected ItemStack item;

    public Element(ItemStack item, ElementRegistry elementRegistry) {
        this.elementRegistry = elementRegistry;
        this.uuid = UUID.randomUUID();

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(Constants.ELEMENT_NBT_KEY, uuid.toString());
        this.item = nbtItem.getItem();

        elementRegistry.getRegistry().put(uuid, this);
    }

    public void destroy() {
        elementRegistry.getRegistry().invalidate(uuid);
    }

}
