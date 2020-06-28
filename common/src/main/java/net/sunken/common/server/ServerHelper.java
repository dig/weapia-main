package net.sunken.common.server;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import java.util.*;

@UtilityClass
public class ServerHelper {

    public static final String SERVER_STORAGE_KEY = "instance";

    public static final String SERVER_ID_KEY = "id";
    public static final String SERVER_TYPE_KEY = "type";
    public static final String SERVER_GAME_KEY = "game";
    public static final String SERVER_WORLD_KEY = "world";

    public static final String SERVER_HOST_KEY = "host";
    public static final String SERVER_PORT_KEY = "port";

    public static final String SERVER_PLAYERS_KEY = "players";
    public static final String SERVER_MAXPLAYERS_KEY = "maxplayers";
    public static final String SERVER_STATE_KEY = "state";

    public static final String SERVER_METADATA_ID_KEY = "metadata-id";
    public static final List<String> SERVER_METADATA_KEYS = Arrays.asList(SERVER_METADATA_ID_KEY);

    public static Server from(Map<String, String> kv) {
        Server.Type type = Server.Type.valueOf(kv.get(SERVER_TYPE_KEY));
        Game game = Game.valueOf(kv.get(SERVER_GAME_KEY));

        Map<String, String> metadata = new HashMap<>();
        for (String key : SERVER_METADATA_KEYS) {
            if (kv.containsKey(key)) {
                metadata.put(key, kv.get(key));
            }
        }

        return Server.builder()
                .id(kv.get(SERVER_ID_KEY))
                .type(type)
                .host(kv.get(SERVER_HOST_KEY))
                .port(Integer.parseInt(kv.get(SERVER_PORT_KEY)))
                .game(game)
                .world(World.valueOf(kv.get(SERVER_WORLD_KEY)))
                .players(Integer.parseInt(kv.get(SERVER_PLAYERS_KEY)))
                .maxPlayers(Integer.parseInt(kv.get(SERVER_MAXPLAYERS_KEY)))
                .state(Server.State.valueOf(kv.get(SERVER_STATE_KEY)))
                .metadata(metadata)
                .build();
    }

    private static final String AB = "abcdefghijklmnopqrstuvwxyz";
    private static Random random = new Random();
    public static String generate(@NonNull Server.Type type) {
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 3; i++) sb.append(random.nextInt(9));
        sb.append(AB.charAt(random.nextInt(AB.length())));
        return type.getPrefix() + sb.toString();
    }
}
