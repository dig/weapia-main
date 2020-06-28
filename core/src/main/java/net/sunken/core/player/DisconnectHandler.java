package net.sunken.core.player;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.database.MongoConnection;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.PlayerManager;
import net.sunken.common.server.packet.ServerDisconnectedPacket;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.PluginInform;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
                        try {
                            corePlayer.save(mongoConnection, player);
                        } catch (Exception e) {
                            log.log(Level.SEVERE, "Failed to save player data.", e);
                        }
                        packetUtil.send(new ServerDisconnectedPacket(player.getUniqueId(), pluginInform.getServer().getId()));
                    });
                });
    }

}
