package net.sunken.core.player;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import lombok.extern.java.Log;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.database.MongoConnection;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.server.packet.ServerDisconnectedPacket;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.PluginInform;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.logging.Level;

import static com.mongodb.client.model.Filters.eq;

@Log
public class DisconnectHandler implements Facet, Listener {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PluginInform pluginInform;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private ConnectHandler connectHandler;
    @Inject
    private MongoConnection mongoConnection;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("");
        playerManager.get(player.getUniqueId())
                .map(CorePlayer.class::cast)
                .ifPresent(corePlayer -> {
                    corePlayer.destroy(player);

                    connectHandler.getPendingConnection().invalidate(corePlayer.getUuid());
                    playerManager.remove(player.getUniqueId());

                    AsyncHelper.executor().execute(() -> {
                        packetUtil.send(new ServerDisconnectedPacket(player.getUniqueId(), pluginInform.getServer().getId()));
                        if (!corePlayer.isSaved()) {
                            try {
                                MongoCollection<Document> collection = mongoConnection.getCollection(DatabaseHelper.DATABASE_MAIN, DatabaseHelper.COLLECTION_PLAYER);
                                collection.updateOne(eq(DatabaseHelper.PLAYER_UUID_KEY, corePlayer.getUuid().toString()),
                                        new Document("$set", corePlayer.toDocument()), new UpdateOptions().upsert(true));
                                corePlayer.setSaved(true);
                            } catch (Exception e) {
                                log.log(Level.SEVERE, "Failed to save player data.", e);
                            }
                        }
                    });
                });
    }

}
