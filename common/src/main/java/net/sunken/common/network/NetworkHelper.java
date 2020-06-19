package net.sunken.common.network;

import lombok.experimental.UtilityClass;
import net.sunken.common.player.PlayerDetail;
import net.sunken.common.player.Rank;

import java.util.Map;
import java.util.UUID;

@UtilityClass
public class NetworkHelper {

    public static final String NETWORK_PLAYER_STORAGE_KEY = "player";

    public static final String NETWORK_PLAYER_UUID_KEY = "uuid";
    public static final String NETWORK_PLAYER_DISPLAYNAME_KEY = "displayname";
    public static final String NETWORK_PLAYER_RANK_KEY = "rank";

    public static PlayerDetail from(Map<String, String> kv) {
        UUID uuid = UUID.fromString(kv.get(NETWORK_PLAYER_UUID_KEY));
        String displayName = kv.get(NETWORK_PLAYER_DISPLAYNAME_KEY);
        Rank rank = Rank.valueOf(kv.get(NETWORK_PLAYER_RANK_KEY));
        return new PlayerDetail(uuid, displayName, rank);
    }

}
