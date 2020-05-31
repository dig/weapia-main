package net.sunken.core.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.java.Log;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.core.Constants;
import net.sunken.core.util.ColourUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

@Log
public class ChatHandler implements Facet, Listener {

    @Inject
    private PlayerManager playerManager;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());

        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();
            Rank rank = abstractPlayer.getRank();

            switch (abstractPlayer.getRank()) {
                case PLAYER:
                    event.setFormat(ColourUtil.fromColourCode(rank.getColourCode()) + abstractPlayer.getUsername() + ChatColor.WHITE + ": " + event.getMessage());
                    break;
                default:
                    event.setFormat(ColourUtil.fromColourCode(rank.getColourCode()) + "[" + rank.getFriendlyName().toUpperCase() + "] " + abstractPlayer.getUsername() + ChatColor.WHITE + ": " + event.getMessage());
            }
        } else {
            player.sendMessage(Constants.FAILED_LOAD_DATA);
            event.setCancelled(true);

            log.severe(String.format("onPlayerChat: Attempted to chat with no data loaded? (%s)", player.getUniqueId().toString()));
        }
    }

}