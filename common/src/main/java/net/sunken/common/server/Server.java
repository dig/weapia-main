package net.sunken.common.server;

import com.google.common.collect.ImmutableMap;
import lombok.*;
import net.sunken.common.database.RedisSerializable;

import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.Map;

@Builder
@ToString
@AllArgsConstructor
public class Server implements RedisSerializable {

    @Getter
    private final String id;
    @Getter
    private final Type type;
    @Getter @Setter
    private Game game;
    @Getter @Setter
    private World world;

    @Getter
    private final String host;
    @Getter
    private final int port;

    @Getter @Setter
    private int players;
    @Getter @Setter
    private int maxPlayers;
    @Getter @Setter
    private State state;

    @Getter
    private Map<String, String> metadata;

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(this.host, this.port);
    }

    public ServerDetail toServerDetail() {
        return new ServerDetail(this.id, this.host, this.port);
    }

    public boolean canJoin() {
        return this.players < this.maxPlayers && this.state == State.OPEN;
    }

    public boolean canHeartbeatCheck() {
        return this.state != State.PENDING && this.type.isHeartbeatCheck();
    }

    @Override
    public Map<String, String> toRedis() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder()
                .put(ServerHelper.SERVER_ID_KEY, id)
                .put(ServerHelper.SERVER_TYPE_KEY, type.toString())
                .put(ServerHelper.SERVER_GAME_KEY, game.toString())
                .put(ServerHelper.SERVER_WORLD_KEY, world.toString())
                .put(ServerHelper.SERVER_HOST_KEY, host)
                .put(ServerHelper.SERVER_PORT_KEY, String.valueOf(port))
                .put(ServerHelper.SERVER_PLAYERS_KEY, String.valueOf(players))
                .put(ServerHelper.SERVER_MAXPLAYERS_KEY, String.valueOf(maxPlayers))
                .put(ServerHelper.SERVER_STATE_KEY, state.toString());

        for (String key : ServerHelper.SERVER_METADATA_KEYS) {
            if (metadata.containsKey(key)) {
                builder.put(key, metadata.get(key));
            }
        }

        return builder.build();
    }

    @Getter
    public enum Type {
        BUNGEE ("bungee", false),
        LOBBY ("lobby", "respects/wep-infrastructure:lobby", "respects/wep-infrastructure-dev:lobby", true),
        INSTANCE ("instance");

        private String prefix;
        private boolean heartbeatCheck;

        private String prodImageUri;
        private String devImageUri;

        private boolean assignId;

        Type(String prefix) {
            this.prefix = prefix;

            this.heartbeatCheck = true;
            this.assignId = false;
        }

        Type(String prefix, String prodImageUri, String devImageUri) {
            this(prefix);
            this.prodImageUri = prodImageUri;
            this.devImageUri = devImageUri;
        }

        Type(String prefix, String prodImageUri, String devImageUri, boolean assignId) {
            this(prefix, prodImageUri, devImageUri);
            this.assignId = assignId;
        }

        Type(String prefix, boolean heartbeatCheck) {
            this(prefix);
            this.heartbeatCheck = heartbeatCheck;
        }

        private static final String AB = "abcdefghijklmnopqrstuvwxyz";
        private static SecureRandom secureRandom = new SecureRandom();

        public String generateId() {
            StringBuilder sb = new StringBuilder(4);

            for (int i = 0; i < 3; i++)
                sb.append(secureRandom.nextInt(9));

            sb.append(AB.charAt(secureRandom.nextInt(AB.length())));

            return this.prefix + sb.toString();
        }
    }

    public enum State {
        PENDING,
        OPEN,
        CLOSED
    }

}
