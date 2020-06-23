package net.sunken.core.hologram;

import net.sunken.common.inject.Facet;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class HologramListener implements Facet, Listener {

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked() instanceof ArmorStand) {
            if (event.getRightClicked().hasMetadata("hologram")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand && event.getEntity().hasMetadata("hologram")) {
            event.setCancelled(true);
        }
    }
}
