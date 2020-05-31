package net.sunken.core.inject;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.inject.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

public abstract class PluginModule extends AbstractModule {

    protected final JavaPlugin plugin;

    public PluginModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(JavaPlugin.class).toInstance(plugin);
        bind(Server.class).toInstance(plugin.getServer());
        bind(PluginManager.class).toInstance(plugin.getServer().getPluginManager());
        bind(FileConfiguration.class).toInstance(plugin.getConfig());
        bind(ProtocolManager.class).toInstance(ProtocolLibrary.getProtocolManager());

        this.configurePlugin();
    }

    public abstract void configurePlugin();
}
