package net.sunken.core.hologram;

import net.sunken.common.inject.Facet;
import net.sunken.core.Constants;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class HologramListener implements Facet, Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked() instanceof ArmorStand
                && event.getRightClicked().hasMetadata(Constants.HOLOGRAM_METADATA_KEY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked() instanceof ArmorStand
                && event.getRightClicked().hasMetadata(Constants.HOLOGRAM_METADATA_KEY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand && event.getEntity().hasMetadata(Constants.HOLOGRAM_METADATA_KEY)) {
            ArmorStand armorStand = (ArmorStand) event.getEntity();
            armorStand.setHealth(armorStand.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByBlockEvent event) {
        if (event.getEntity() instanceof ArmorStand && event.getEntity().hasMetadata(Constants.HOLOGRAM_METADATA_KEY)) {
            ArmorStand armorStand = (ArmorStand) event.getEntity();
            armorStand.setHealth(armorStand.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onManipulate(PlayerArmorStandManipulateEvent event) {
        if (event.getRightClicked().hasMetadata(Constants.HOLOGRAM_METADATA_KEY)) {
            event.setCancelled(true);
        }
    }
}
