package net.sunken.bungeecord.player;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.sunken.bungeecord.BungeeInform;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.player.packet.PlayerProxyQuitPacket;
import net.sunken.common.server.packet.ServerDisconnectedPacket;
import net.sunken.common.util.AsyncHelper;

import java.util.Optional;

@Log
public class DisconnectHandler implements Facet, Listener {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private BungeeInform bungeeInform;

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());

        packetUtil.send(new PlayerProxyQuitPacket(player.getUniqueId()));
        packetUtil.send(new ServerDisconnectedPacket(player.getUniqueId(), bungeeInform.getServer().getId()));

        if (abstractPlayerOptional.isPresent()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();

            AsyncHelper.executor().submit(() -> {
               boolean saveState = bungeePlayer.save();

               if (!saveState) {
                   log.severe(String.format("Unable to save data. (%s)", bungeePlayer.getUuid().toString()));
               }

               playerManager.remove(bungeePlayer.getUuid());
            });
        }
    }

}
