package net.sunken.core.player;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.server.packet.ServerDisconnectedPacket;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.PluginInform;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

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

    //--- Event is called last.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());

        packetUtil.send(new ServerDisconnectedPacket(player.getUniqueId(), pluginInform.getServer().getId()));
        log.info(String.format("onPlayerQuit (%s)", player.getUniqueId().toString()));

        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            connectHandler.getPendingConnection().invalidate(abstractPlayer);

            event.setQuitMessage("");
            abstractPlayer.destroy();

            AsyncHelper.executor().submit(() -> {
                boolean saveState = abstractPlayer.save();

                if (!saveState) {
                    log.severe(String.format("Unable to save data. (%s)", abstractPlayer.getUuid().toString()));
                }

                playerManager.remove(player.getUniqueId());
            });
        } else {
            log.severe(String.format("onPlayerQuit: Attempted to quit with no data loaded? (%s)", player.getUniqueId().toString()));
        }
    }

}
