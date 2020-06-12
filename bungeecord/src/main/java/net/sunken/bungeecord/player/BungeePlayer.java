package net.sunken.bungeecord.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.server.ServerDetail;

import java.util.Optional;
import java.util.UUID;

@ToString
public class BungeePlayer extends AbstractPlayer {

    @Getter @Setter
    private Optional<ServerDetail> serverConnectedTo;
    @Getter @Setter
    private Optional<ServerDetail> serverTarget;

    private String lastSentMessage = "";
    private Long lastSentMessageTime = 0L;

    public BungeePlayer(@NonNull UUID uuid, @NonNull String username) {
        super(uuid, username);
        this.serverConnectedTo = Optional.empty();
        this.serverTarget = Optional.empty();
    }

    public BungeePlayer(@NonNull ProxiedPlayer proxiedPlayer) {
        super(proxiedPlayer.getUniqueId(), proxiedPlayer.getDisplayName());
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public boolean save() {
        return true;
    }

    @Override
    public void setup() {
    }

    @Override
    public void destroy() {
    }

    public Optional<ProxiedPlayer> toProxiedPlayer() {
        return ProxyServer.getInstance().getPlayers().stream()
                .filter(proxiedPlayer -> proxiedPlayer.getUniqueId().equals(uuid))
                .findFirst();
    }

    public void connect(ServerDetail serverDetail) {
        Optional<ProxiedPlayer> playerOptional = toProxiedPlayer();

        if (playerOptional.isPresent()) {
            ProxiedPlayer player = playerOptional.get();
            player.connect(ProxyServer.getInstance().constructServerInfo(serverDetail.getId(), serverDetail.toInetSocketAddress(), "", false));
        }
    }

    public boolean canSendMessage(String newMessage, Long delay) {
        if (lastSentMessage.equalsIgnoreCase(newMessage) || System.currentTimeMillis() < (lastSentMessageTime + delay)) {
            return false;
        }

        lastSentMessage = newMessage;
        lastSentMessageTime = System.currentTimeMillis();
        return true;
    }

}
