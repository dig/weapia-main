package net.sunken.core.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.extern.java.Log;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.database.MongoConnection;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.server.packet.ServerConnectedPacket;
import net.sunken.common.util.AsyncHelper;
import net.sunken.common.util.Tuple;
import net.sunken.core.Constants;
import net.sunken.core.PluginInform;
import net.sunken.core.engine.EngineManager;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

@Log
@Singleton
public class ConnectHandler implements Facet, Listener {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PluginInform pluginInform;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private EngineManager engineManager;
    @Inject
    private MongoConnection mongoConnection;

    @Getter
    private Cache<UUID, AbstractPlayer> pendingConnection;

    public ConnectHandler() {
        pendingConnection = CacheBuilder.newBuilder()
                .expireAfterWrite(10L, TimeUnit.SECONDS)
                .build();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("");

        AbstractPlayer abstractPlayer = pendingConnection.getIfPresent(player.getUniqueId());
        if (abstractPlayer != null) {
            playerManager.add(abstractPlayer);
            pendingConnection.invalidate(player.getUniqueId());

            abstractPlayer.setup();
            AsyncHelper.executor().submit(() -> packetUtil.send(new ServerConnectedPacket(player.getUniqueId(), pluginInform.getServer().getId())));
        } else {
            player.kickPlayer(Constants.FAILED_LOAD_DATA);
        }
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        AbstractPlayer abstractPlayer = engineManager.getGameMode().getPlayerMapper().apply(new Tuple<>(event.getUniqueId(), event.getName()));

        boolean loadState = true;
        try {
            MongoCollection<Document> collection = mongoConnection.getCollection(DatabaseHelper.DATABASE_MAIN, DatabaseHelper.COLLECTION_PLAYER);
            Document document = collection.find(eq(DatabaseHelper.PLAYER_UUID_KEY, event.getUniqueId().toString())).first();
            if (document != null) {
                loadState = abstractPlayer.fromDocument(document);
            }
        } catch (MongoException e) {
            loadState = false;
        }

        if (loadState) {
            pendingConnection.put(abstractPlayer.getUuid(), abstractPlayer);
            event.allow();
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Constants.FAILED_LOAD_DATA);
        }
    }

}
