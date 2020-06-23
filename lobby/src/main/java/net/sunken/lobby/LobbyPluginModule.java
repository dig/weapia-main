package net.sunken.lobby;

import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.common.server.module.ServerModule;
import net.sunken.core.CoreModule;
import net.sunken.core.engine.EngineModule;
import net.sunken.core.inject.PluginModule;
import net.sunken.lobby.config.LobbyConfiguration;
import net.sunken.lobby.config.UIConfiguration;
import net.sunken.lobby.inventory.InventoryModule;
import net.sunken.lobby.player.ServerUpdatedListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LobbyPluginModule extends PluginModule {

    public LobbyPluginModule(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void configurePlugin() {
        install(new ConfigModule(new File("config/main.conf"), LobbyConfiguration.class));
        install(new ConfigModule(new File("config/ui.conf"), UIConfiguration.class));

        install(new CoreModule());
        install(new ServerModule());
        install(new EngineModule());
        install(new InventoryModule());

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(ServerUpdatedListener.class);
    }
}
