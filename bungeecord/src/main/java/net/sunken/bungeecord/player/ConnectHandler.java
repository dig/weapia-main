package net.sunken.bungeecord.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import lombok.extern.java.Log;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.sunken.bungeecord.Constants;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.database.MongoConnection;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.network.NetworkManager;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.packet.expectation.ExpectationFactory;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.player.packet.*;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerDetail;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.util.AsyncHelper;
import org.bson.Document;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

@Log
public class ConnectHandler implements Facet, Listener, Enableable {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private NetworkManager networkManager;
    @Inject
    private ServerManager serverManager;
    @Inject
    private net.md_5.bungee.api.plugin.Plugin plugin;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private ExpectationFactory expectationFactory;
    @Inject
    private MongoConnection mongoConnection;

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private PlayerSendToServerHandler playerSendToServerHandler;
    @Inject
    private PlayerProxyMessageHandler playerProxyMessageHandler;

    private Cache<UUID, BungeePlayer> pendingPlayerConnection;

    @EventHandler
    public void onLogin(LoginEvent event) {
        PendingConnection pendingConnection = event.getConnection();
        BungeePlayer bungeePlayer = new BungeePlayer(pendingConnection.getUniqueId(), pendingConnection.getName());

        event.registerIntent(plugin);
        AsyncHelper.executor().submit(() -> {
            MongoCollection<Document> collection = mongoConnection.getCollection(DatabaseHelper.DATABASE_MAIN, DatabaseHelper.COLLECTION_PLAYER);
            boolean loadState = bungeePlayer.fromDocument(collection.find(eq("uuid", pendingConnection.getUniqueId().toString())).first());

            packetUtil.sendSync(new PlayerRequestServerPacket(pendingConnection.getUniqueId(), Server.Type.LOBBY, false));
            boolean success = expectationFactory.waitFor(packet -> {
                if (packet instanceof PlayerSendToServerPacket) {
                    PlayerSendToServerPacket playerSendToServerPacket = (PlayerSendToServerPacket) packet;

                    if (playerSendToServerPacket.getUuid().equals(pendingConnection.getUniqueId())) {
                        bungeePlayer.setServerTarget(Optional.of(playerSendToServerPacket.getServerDetail()));
                        return true;
                    }
                }

                return false;
            }, 15 * 10, 100);

            if (!loadState) {
                event.setCancelReason(TextComponent.fromLegacyText(Constants.FAILED_LOAD_DATA));
                event.setCancelled(true);
            }

            if (!success) {
                event.setCancelReason(TextComponent.fromLegacyText(Constants.FAILED_FIND_SERVER));
                event.setCancelled(true);
            }

            if (loadState && success) {
                pendingPlayerConnection.put(bungeePlayer.getUuid(), bungeePlayer);
            }

            event.completeIntent(plugin);
        });
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (pendingPlayerConnection.getIfPresent(player.getUniqueId()) != null) {
            BungeePlayer bungeePlayer = pendingPlayerConnection.getIfPresent(player.getUniqueId());

            AsyncHelper.executor().submit(() -> networkManager.add(bungeePlayer.toPlayerDetail(), false));
            playerManager.add(bungeePlayer);
            pendingPlayerConnection.invalidate(player.getUniqueId());
        }

        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());
        if (!abstractPlayerOptional.isPresent()) {
            player.disconnect(TextComponent.fromLegacyText(Constants.FAILED_LOAD_DATA));
        } else {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();

            switch (event.getReason()) {
                case JOIN_PROXY:
                    Optional<ServerDetail> serverConnectedTo = bungeePlayer.getServerConnectedTo();
                    Optional<ServerDetail> serverTarget = bungeePlayer.getServerTarget();

                    if (!serverConnectedTo.isPresent() && serverTarget.isPresent()) {
                        ServerDetail serverTargetDetail = serverTarget.get();
                        ServerInfo serverInfo = plugin.getProxy().constructServerInfo(serverTargetDetail.getId(), serverTargetDetail.toInetSocketAddress(), "", false);

                        event.setTarget(serverInfo);

                        bungeePlayer.setServerConnectedTo(Optional.of(serverTargetDetail));
                        bungeePlayer.setServerTarget(Optional.empty());
                    } else {
                        player.disconnect(TextComponent.fromLegacyText(Constants.FAILED_FIND_SERVER));
                    }
                    break;
                case SERVER_DOWN_REDIRECT:
                case LOBBY_FALLBACK:
                case KICK_REDIRECT:
                    Optional<Server> availableLobbyOptional = serverManager.findAvailable(Server.Type.LOBBY, Game.NONE);

                    if (availableLobbyOptional.isPresent()) {
                        Server availableLobby = availableLobbyOptional.get();

                        event.setTarget(plugin.getProxy().constructServerInfo(availableLobby.getId(), availableLobby.toInetSocketAddress(), "", false));
                        bungeePlayer.setServerConnectedTo(Optional.of(availableLobby.toServerDetail()));
                    } else {
                        player.disconnect(TextComponent.fromLegacyText(Constants.FAILED_FIND_SERVER));
                    }
                    break;
                case PLUGIN_MESSAGE:
                case UNKNOWN:
                case COMMAND:
                    player.sendMessage(TextComponent.fromLegacyText(Constants.FAILED_SERVER_CONNECT));
                    event.setCancelled(true);
                    break;
                case PLUGIN:
                    Optional<Server> serverOptional = serverManager.findServerById(event.getTarget().getName());
                    if (serverOptional.isPresent()) {
                        Server server = serverOptional.get();
                        bungeePlayer.setServerConnectedTo(Optional.of(server.toServerDetail()));
                    }
                    break;
            }

        }
    }

    @EventHandler
    public void onKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo kickedFrom = event.getKickedFrom();

        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());
        Optional<Server> availableLobbyOptional = serverManager.findAvailable(Server.Type.LOBBY, Game.NONE, Arrays.asList(kickedFrom.getName()));

        if (abstractPlayerOptional.isPresent() && availableLobbyOptional.isPresent()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
            Server availableLobby = availableLobbyOptional.get();

            event.setCancelServer(plugin.getProxy().constructServerInfo(availableLobby.getId(), availableLobby.toInetSocketAddress(), "", false));
            bungeePlayer.setServerConnectedTo(Optional.of(availableLobby.toServerDetail()));
        } else {
            player.disconnect(TextComponent.fromLegacyText(Constants.FAILED_FIND_SERVER));
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo serverInfo = event.getServer().getInfo();
        Optional<Server> serverOptional = serverManager.findServerById(serverInfo.getName());

        if (serverOptional.isPresent() && serverOptional.get().getType() == Server.Type.INSTANCE) {
            player.sendMessage(TextComponent.fromLegacyText(String.format(Constants.PLAYER_SEND_SERVER, serverInfo.getName())));
        }
    }

    @EventHandler
    public void onPlayerLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        Constants.PLAYER_WELCOME_MESSAGE.forEach(message -> player.sendMessage(TextComponent.fromLegacyText(message)));

        TextComponent spaceBeforeLink = new TextComponent("      ");
        TextComponent firstGameLink = new TextComponent("[Herobrine Escort]");
        firstGameLink.setColor(ChatColor.YELLOW);
        firstGameLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server herobrine_escort"));

        TextComponent secondGameLink = new TextComponent("[Space Games]");
        secondGameLink.setColor(ChatColor.DARK_PURPLE);
        secondGameLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server space_games_solo"));

        TextComponent spaceBeforeLink1 = new TextComponent("                            ");
        TextComponent thirdGameLink = new TextComponent("[Base Invaders]");
        thirdGameLink.setColor(ChatColor.AQUA);
        thirdGameLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server base_invaders"));

        TextComponent fourthGameLink = new TextComponent("[Natural Disaster]");
        fourthGameLink.setColor(ChatColor.RED);
        fourthGameLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server natural_disaster"));

        spaceBeforeLink.addExtra(firstGameLink);
        spaceBeforeLink.addExtra(" ");
        spaceBeforeLink.addExtra(secondGameLink);
        spaceBeforeLink.addExtra(" ");
        spaceBeforeLink.addExtra(thirdGameLink);

        spaceBeforeLink1.addExtra(fourthGameLink);

        player.sendMessage(spaceBeforeLink);
        player.sendMessage(spaceBeforeLink1);
        player.sendMessage(new TextComponent(" "));
    }

    @Override
    public void enable() {
        pendingPlayerConnection = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .build();

        packetHandlerRegistry.registerHandler(PlayerSendToServerPacket.class, playerSendToServerHandler);
        packetHandlerRegistry.registerHandler(PlayerProxyMessagePacket.class, playerProxyMessageHandler);
    }

    @Override
    public void disable() {
    }

}
