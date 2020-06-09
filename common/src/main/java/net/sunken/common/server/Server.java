package net.sunken.common.server;

import lombok.*;

import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.Map;

@Builder
@ToString
@AllArgsConstructor
public class Server {

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
