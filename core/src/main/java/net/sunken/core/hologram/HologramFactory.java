package net.sunken.core.hologram;

import com.google.inject.Inject;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class HologramFactory {

    @Inject
    private JavaPlugin plugin;

    public Hologram createHologram(Location location, List<String> lines) {
        return new Hologram(location, lines, plugin);
    }

}
