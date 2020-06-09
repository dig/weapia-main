package net.sunken.core.item.impl;

import com.google.inject.Inject;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.sunken.core.item.ItemRegistry;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class AnItemInstanceFactory {

    @Inject
    private ItemRegistry itemRegistry;

    public Optional<AnItemInstance> create(ItemStack itemStack) {
        Optional<AnItem> anItemOptional = itemRegistry.getItem(itemStack);
        if (anItemOptional.isPresent()) {
            AnItem anItem = anItemOptional.get();

            AnItemAttributes attributes = new AnItemAttributes();
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.getKeys().forEach(key -> attributes.set(key, nbtItem.getObject(key, Object.class)));

            return Optional.of(new AnItemInstance(anItem, attributes, itemStack));
        }

        return Optional.empty();
    }

}
