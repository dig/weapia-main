package net.sunken.common.player;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.ToString;
import net.sunken.common.database.RedisSerializable;
import net.sunken.common.network.NetworkHelper;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Data
@ToString
public class PlayerDetail implements Serializable, RedisSerializable {

    private final UUID uuid;
    private final String displayName;
    private final Rank rank;

    @Override
    public Map<String, String> toRedis() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder()
                .put(NetworkHelper.NETWORK_PLAYER_UUID_KEY, uuid.toString())
                .put(NetworkHelper.NETWORK_PLAYER_DISPLAYNAME_KEY, displayName)
                .put(NetworkHelper.NETWORK_PLAYER_RANK_KEY, rank.toString());
        return builder.build();
    }

}
