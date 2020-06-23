package net.sunken.common.database;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DatabaseHelper {

    public final static String DATABASE_MAIN = "weapia";

    public final static String COLLECTION_PLAYER = "player";
    public final static String PLAYER_UUID_KEY = "uuid";
    public final static String PLAYER_USERNAME_KEY = "username";
    public final static String PLAYER_RANK_KEY = "rank";
    public final static String PLAYER_FIRSTLOGIN_KEY = "firstlogin";
    public final static String PLAYER_LASTLOGIN_KEY = "lastlogin";

    public final static String LOCATION_WORLD_KEY = "world";
    public final static String LOCATION_X_KEY = "x";
    public final static String LOCATION_Y_KEY = "y";
    public final static String LOCATION_Z_KEY = "z";
    public final static String LOCATION_YAW_KEY = "yaw";
    public final static String LOCATION_PITCH_KEY = "pitch";

    public final static String INVENTORY_HELD_SLOT_KEY = "heldslot";
    public final static String INVENTORY_CONTENTS_KEY = "contents";
    public final static String INVENTORY_ARMOUR_CONTENTS_KEY = "armourcontents";
    public final static String INVENTORY_EXTRA_CONTENTS_KEY = "extracontents";
    public final static String INVENTORY_STORAGE_CONTENTS_KEY = "storagecontents";
    public final static String INVENTORY_SIZE_KEY = "size";

    public final static String PLAYER_INVENTORY_KEY = "inventory";
    public final static String PLAYER_LOCATION_KEY = "location";
    public final static String PLAYER_HEALTH_LEVEL_KEY = "health";
    public final static String PLAYER_FOOD_LEVEL_KEY = "food";
    public final static String PLAYER_FIRE_TICK_KEY = "firetick";
    public final static String PLAYER_GAMEMODE_KEY = "gamemode";
    public final static String PLAYER_EXPERIENCE_KEY = "exp";
    public final static String PLAYER_ENDERCHEST_KEY = "enderchest";
    public final static String PLAYER_POTIONEFFECTS_KEY = "potioneffects";

    public final static String PLAYER_SURVIVAL_REALMS_KEY = "survivalrealms";

    public final static String PLAYER_SURVIVAL_REALMS_ADVENTURE_KEY = "adventure";
    public final static String PLAYER_SURVIVAL_REALMS_WORLD_KEY = "world";
    public final static String PLAYER_SURVIVAL_REALMS_LOCATION_KEY = "location";

    public final static String PLAYER_SURVIVAL_REALMS_PLAYER_KEY = "player";
    public final static String PLAYER_SURVIVAL_REALMS_INSTANCE_KEY = "instance";
    public final static String PLAYER_SURVIVAL_REALMS_COINS_KEY = "coins";

    public final static String GRIDFS_BUCKET_SURVIVAL_REALMS = "srworld";

}
