package net.sunken.core.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.sunken.common.database.DatabaseHelper;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.PlayerInventory;

@UtilityClass
public class MongoBukkitUtil {

    public static Location location(@NonNull Document document, boolean containWorld) {
        return new Location(
                containWorld ? Bukkit.getWorld(document.getString(DatabaseHelper.LOCATION_WORLD_KEY)) : null,
                document.getDouble(DatabaseHelper.LOCATION_X_KEY),
                document.getDouble(DatabaseHelper.LOCATION_Y_KEY),
                document.getDouble(DatabaseHelper.LOCATION_Z_KEY),
                document.getDouble(DatabaseHelper.LOCATION_YAW_KEY).floatValue(),
                document.getDouble(DatabaseHelper.LOCATION_PITCH_KEY).floatValue());
    }

    public static Document location(@NonNull Location location, boolean containWorld) {
        Document document = new Document()
                .append(DatabaseHelper.LOCATION_X_KEY, location.getX())
                .append(DatabaseHelper.LOCATION_Y_KEY, location.getY())
                .append(DatabaseHelper.LOCATION_Z_KEY, location.getZ())
                .append(DatabaseHelper.LOCATION_YAW_KEY, location.getYaw())
                .append(DatabaseHelper.LOCATION_PITCH_KEY, location.getPitch());

        if (containWorld) document.append(DatabaseHelper.LOCATION_WORLD_KEY, location.getWorld().getName());
        return document;
    }

    public static Document inventory(@NonNull PlayerInventory inventory) {
        return new Document()
                .append(DatabaseHelper.INVENTORY_HELD_SLOT_KEY, inventory.getHeldItemSlot())
                .append(DatabaseHelper.INVENTORY_CONTENTS_KEY, InventoryUtil.encode(inventory.getContents()))
                .append(DatabaseHelper.INVENTORY_ARMOUR_CONTENTS_KEY, InventoryUtil.encode(inventory.getArmorContents()))
                .append(DatabaseHelper.INVENTORY_EXTRA_CONTENTS_KEY, InventoryUtil.encode(inventory.getExtraContents()))
                .append(DatabaseHelper.INVENTORY_STORAGE_CONTENTS_KEY, InventoryUtil.encode(inventory.getStorageContents()));
    }

    public static void inventory(@NonNull Document document, @NonNull PlayerInventory inventory) {
        inventory.setHeldItemSlot(document.getInteger(DatabaseHelper.INVENTORY_HELD_SLOT_KEY));
        if (document.containsKey(DatabaseHelper.INVENTORY_CONTENTS_KEY)) {
            inventory.setContents(InventoryUtil.decode(document.getString(DatabaseHelper.INVENTORY_CONTENTS_KEY)));
        }
        if (document.containsKey(DatabaseHelper.INVENTORY_ARMOUR_CONTENTS_KEY)) {
            inventory.setArmorContents(InventoryUtil.decode(document.getString(DatabaseHelper.INVENTORY_ARMOUR_CONTENTS_KEY)));
        }
        if (document.containsKey(DatabaseHelper.INVENTORY_EXTRA_CONTENTS_KEY)) {
            inventory.setExtraContents(InventoryUtil.decode(document.getString(DatabaseHelper.INVENTORY_EXTRA_CONTENTS_KEY)));
        }
        if (document.containsKey(DatabaseHelper.INVENTORY_STORAGE_CONTENTS_KEY)) {
            inventory.setStorageContents(InventoryUtil.decode(document.getString(DatabaseHelper.INVENTORY_STORAGE_CONTENTS_KEY)));
        }
    }
}
