package net.sunken.core.item;

import de.tr7zw.changeme.nbtapi.*;
import org.bukkit.inventory.*;

import java.util.*;

public class ItemRegistry {

    private static final String REGISTERED_ITEM_KEY = "itemConfigName";

    /** ID -> AnItem */
    private final Map<String, AnItem> items = new HashMap<>();

    public boolean isRegistered(String id) {
        return items.containsKey(id);
    }

    public boolean isRegistered(ItemStack itemStack) {
        NBTItem itemStackNBT = new NBTItem(itemStack);

        return itemStackNBT.hasKey(REGISTERED_ITEM_KEY) &&
                isRegistered(itemStackNBT.getString(REGISTERED_ITEM_KEY));
    }

    public void register(AnItem anItem) {
        items.put(anItem.getId(), anItem);
    }

    public Optional<AnItem> getItem(String id) {
        return Optional.ofNullable(items.get(id));
    }
}
