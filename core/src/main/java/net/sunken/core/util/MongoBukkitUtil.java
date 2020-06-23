package net.sunken.core.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.util.MongoUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

@UtilityClass
public class MongoBukkitUtil {

    public static Document fromLocation(@NonNull Location location, boolean containWorld) {
        Document document = new Document()
                .append(DatabaseHelper.LOCATION_X_KEY, location.getX())
                .append(DatabaseHelper.LOCATION_Y_KEY, location.getY())
                .append(DatabaseHelper.LOCATION_Z_KEY, location.getZ())
                .append(DatabaseHelper.LOCATION_YAW_KEY, location.getYaw())
                .append(DatabaseHelper.LOCATION_PITCH_KEY, location.getPitch());

        if (document.containsKey(DatabaseHelper.LOCATION_WORLD_KEY)) {
            document.append(DatabaseHelper.LOCATION_WORLD_KEY, location.getWorld().getName());
        }
        return document;
    }

    public static Location toLocation(@NonNull Document document) {
        return new Location(
                document.containsKey(DatabaseHelper.LOCATION_WORLD_KEY) ? Bukkit.getWorld(document.getString(DatabaseHelper.LOCATION_WORLD_KEY)) : null,
                document.getDouble(DatabaseHelper.LOCATION_X_KEY),
                document.getDouble(DatabaseHelper.LOCATION_Y_KEY),
                document.getDouble(DatabaseHelper.LOCATION_Z_KEY),
                document.getDouble(DatabaseHelper.LOCATION_YAW_KEY).floatValue(),
                document.getDouble(DatabaseHelper.LOCATION_PITCH_KEY).floatValue());
    }

    public static Document fromInventory(@NonNull PlayerInventory inventory) {
        return new Document()
                .append(DatabaseHelper.INVENTORY_HELD_SLOT_KEY, inventory.getHeldItemSlot())
                .append(DatabaseHelper.INVENTORY_CONTENTS_KEY, InventoryUtil.encode(inventory.getContents()))
                .append(DatabaseHelper.INVENTORY_ARMOUR_CONTENTS_KEY, InventoryUtil.encode(inventory.getArmorContents()))
                .append(DatabaseHelper.INVENTORY_EXTRA_CONTENTS_KEY, InventoryUtil.encode(inventory.getExtraContents()))
                .append(DatabaseHelper.INVENTORY_STORAGE_CONTENTS_KEY, InventoryUtil.encode(inventory.getStorageContents()));
    }

    public static void setInventory(@NonNull Document document, @NonNull PlayerInventory inventory) {
        inventory.setHeldItemSlot(document.getInteger(DatabaseHelper.INVENTORY_HELD_SLOT_KEY));
        inventory.setContents(InventoryUtil.decode(document.getString(DatabaseHelper.INVENTORY_CONTENTS_KEY)));
        inventory.setArmorContents(InventoryUtil.decode(document.getString(DatabaseHelper.INVENTORY_ARMOUR_CONTENTS_KEY)));
        inventory.setExtraContents(InventoryUtil.decode(document.getString(DatabaseHelper.INVENTORY_EXTRA_CONTENTS_KEY)));
        inventory.setStorageContents(InventoryUtil.decode(document.getString(DatabaseHelper.INVENTORY_STORAGE_CONTENTS_KEY)));
    }

    public static Document fromPlayer(@NonNull Player player, boolean containLocation, boolean containWorld) {
        Document document = new Document()
                .append(DatabaseHelper.PLAYER_HEALTH_LEVEL, player.getHealth())
                .append(DatabaseHelper.PLAYER_FOOD_LEVEL, player.getFoodLevel())
                .append(DatabaseHelper.PLAYER_GAMEMODE, player.getGameMode().toString())
                .append(DatabaseHelper.PLAYER_INVENTORY_KEY, fromInventory(player.getInventory()));

        if (containLocation) {
            document.append(DatabaseHelper.PLAYER_LOCATION, fromLocation(player.getLocation(), containWorld));
        }
        return document;
    }

    public static void setPlayer(@NonNull Player player, @NonNull Document document) {
        player.setHealth(document.getDouble(DatabaseHelper.PLAYER_HEALTH_LEVEL));
        player.setFoodLevel(document.getInteger(DatabaseHelper.PLAYER_FOOD_LEVEL, 20));
        player.setGameMode((GameMode) MongoUtil.getEnumOrDefault(document, GameMode.class, DatabaseHelper.PLAYER_GAMEMODE, GameMode.SURVIVAL));

        Document inventoryDocument = (Document) document.get(DatabaseHelper.PLAYER_INVENTORY_KEY);
        setInventory(inventoryDocument, player.getInventory());

        if (document.containsKey(DatabaseHelper.PLAYER_LOCATION)) {
            Document locationDocument = (Document) document.get(DatabaseHelper.PLAYER_LOCATION);
            player.teleport(toLocation(locationDocument));
        }
    }
}
