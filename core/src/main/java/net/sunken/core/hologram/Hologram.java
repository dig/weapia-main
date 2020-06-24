package net.sunken.core.hologram;

import lombok.Getter;
import net.sunken.core.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Hologram {

    private static final double OFFSET_Y = 0.28;

    @Getter
    private Location location;
    @Getter
    private List<String> lines;

    private JavaPlugin plugin;
    private List<UUID> livingEntities;

    public Hologram(Location location, List<String> lines, JavaPlugin plugin) {
        this.location = location;
        this.lines = lines;
        this.plugin = plugin;
        this.livingEntities = new ArrayList<>();
        this.setup();
    }

    private void setup() {
        double y = 0;
        for (String line : lines) {
            ArmorStand entity = (ArmorStand) this.location.getWorld().spawnEntity(location.clone().add(0, y, 0), EntityType.ARMOR_STAND);
            entity.setVisible(false);
            entity.setCustomNameVisible(true);
            entity.setSmall(true);
            entity.setBasePlate(true);
            entity.setGravity(false);
            entity.setCustomName(ChatColor.translateAlternateColorCodes('&', line));
            entity.setMetadata(Constants.HOLOGRAM_METADATA_KEY, new FixedMetadataValue(plugin, true));
            livingEntities.add(entity.getUniqueId());

            y -= OFFSET_Y;
        }
    }

    public void update(int index, String line) {
        if (index >= 0 && index < lines.size()) {
            lines.set(index, line);

            Entity entity = Bukkit.getEntity(livingEntities.get(index));
            if (entity != null) {
                entity.setCustomName(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
    }

    public void teleport(Location location) {
        double y = 0;
        for (UUID uuid : livingEntities) {
            Entity entity = Bukkit.getEntity(uuid);
            entity.teleport(location.clone().add(0, y, 0));
            y += OFFSET_Y;
        }
    }

    public void remove() {
        for (UUID uuid : livingEntities) {
            Entity entity = Bukkit.getEntity(uuid);
            entity.remove();
        }
        livingEntities.clear();
    }

    public void refresh() {
        remove();
        setup();
    }
}
