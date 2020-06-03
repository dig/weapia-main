package net.sunken.core.item;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.tr7zw.changeme.nbtapi.*;
import lombok.extern.java.Log;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.core.Constants;
import net.sunken.core.inventory.ItemBuilder;
import net.sunken.core.item.config.AnItemConfiguration;
import net.sunken.core.item.config.AnItemsConfiguration;
import net.sunken.core.item.config.ItemAttributeConfiguration;
import net.sunken.core.item.impl.AnItem;
import net.sunken.core.item.impl.AnItemListener;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.ChatColor;
import org.bukkit.inventory.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Singleton
public class ItemRegistry implements Facet, Enableable {

    @Inject
    private Injector injector;

    /** ID -> AnItem */
    private final Map<String, AnItem> items = new HashMap<>();

    public boolean isRegistered(String id) {
        return items.containsKey(id);
    }

    public boolean isRegistered(ItemStack itemStack) {
        NBTItem itemStackNBT = new NBTItem(itemStack);

        return itemStackNBT.hasKey(Constants.ITEM_NBT_KEY) &&
                isRegistered(itemStackNBT.getString(Constants.ITEM_NBT_KEY));
    }

    public void register(AnItem anItem) {
        items.put(anItem.getId(), anItem);
        log.info(String.format("Registered item: %s", anItem.getId()));
    }

    public void register(AnItemConfiguration anItemConfiguration) {
        ItemBuilder itemBuilder = new ItemBuilder(anItemConfiguration.getMaterial())
                .name(ChatColor.translateAlternateColorCodes('&', anItemConfiguration.getDisplayName()))
                .lores(anItemConfiguration.getLore().stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));

        AnItemListener listener = null;
        if (anItemConfiguration.getListener() != null) {
            try {
                Class cls = Class.forName(anItemConfiguration.getListener());
                listener = (AnItemListener) injector.getInstance(cls);
            } catch (ClassNotFoundException | ClassCastException e) {
                log.info(String.format("Unable to register listener. (%s)", anItemConfiguration.getId()));
            }
        }

        AnItem anItem = new AnItem(anItemConfiguration.getId(), itemBuilder, listener, anItemConfiguration.isStack());
        List<ItemAttributeConfiguration> attributeConfigurations = anItemConfiguration.getAttributes();
        if (attributeConfigurations != null) {
            attributeConfigurations.forEach(itemAttributeConfiguration -> anItem.getAttributes().set(itemAttributeConfiguration.getKey(), itemAttributeConfiguration.getValue()));
        }

        register(anItem);
    }

    public Optional<AnItem> getItem(String id) {
        return Optional.ofNullable(items.get(id));
    }
    public Optional<AnItem> getItem(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.getKeys().contains(Constants.ITEM_NBT_KEY)) {
            String configName = nbtItem.getString(Constants.ITEM_NBT_KEY);
            return getItem(configName);
        }

        return Optional.empty();
    }

    @Override
    public void enable() {
        File itemDirectory = new File("config/item");
        if (itemDirectory.exists()) {
            File[] itemFiles = itemDirectory.listFiles((dir, name) -> name.endsWith(".conf"));
            for (File itemFile : itemFiles) {
                AnItemsConfiguration anItemsConfiguration = loadConfig(itemFile, AnItemsConfiguration.class);
                if (anItemsConfiguration != null) {
                    anItemsConfiguration.getItems().forEach(this::register);
                }
            }
        }
    }

    @Override
    public void disable() {
    }

    private <T> T loadConfig(File configFile, Class<T> type) {
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();

        try {
            ConfigurationNode rootNode = loader.load();
            return rootNode.getValue(TypeToken.of(type));
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
            log.severe(String.format("Unable to load item config file. (%s)", configFile.getName()));
        }

        return null;
    }

}
