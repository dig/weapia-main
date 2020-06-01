package net.sunken.core.item;

import lombok.*;
import org.bukkit.*;
import org.bukkit.inventory.*;

import java.util.*;

@Data
public class AnItem {

    private final String id;

    private final Material material;

    private final Map<String, Object> attributes = new HashMap<>();

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material);
        return itemStack;
    }
}
