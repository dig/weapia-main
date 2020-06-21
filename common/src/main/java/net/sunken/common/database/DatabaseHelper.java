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

    public final static String PLAYER_SURVIVAL_REALMS_KEY = "survivalrealms";
    public final static String PLAYER_SURVIVAL_REALMS_WORLD_KEY = "world";
    public final static String PLAYER_SURVIVAL_REALMS_LOCATION_KEY = "location";
    public final static String PLAYER_SURVIVAL_REALMS_COINS_KEY = "coins";

    public final static String GRIDFS_BUCKET_SURVIVAL_REALMS = "srworld";

}
