package net.sunken.bungeecord.player;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import lombok.extern.java.Log;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.database.MongoConnection;
import net.sunken.common.inject.Facet;
import net.sunken.common.network.NetworkManager;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.util.AsyncHelper;
import org.bson.Document;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

@Log
public class DisconnectHandler implements Facet, Listener {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private NetworkManager networkManager;
    @Inject
    private MongoConnection mongoConnection;

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());

        if (abstractPlayerOptional.isPresent()) {
            AsyncHelper.executor().submit(() -> {
                BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();

                networkManager.remove(bungeePlayer.toPlayerDetail(), false);
                playerManager.remove(bungeePlayer.getUuid());

                MongoCollection<Document> collection = mongoConnection.getCollection(DatabaseHelper.DATABASE_MAIN, DatabaseHelper.COLLECTION_PLAYER);
                collection.updateOne(eq("uuid", bungeePlayer.getUuid().toString()),
                        new Document("$set", bungeePlayer.toDocument()), new UpdateOptions().upsert(true));
            });
        }
    }

}
