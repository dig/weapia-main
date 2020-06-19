package net.sunken.core.inject;

import com.google.inject.*;
import lombok.extern.java.Log;
import net.sunken.common.command.impl.BaseCommand;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.event.EventManager;
import net.sunken.common.event.SunkenListener;
import net.sunken.common.inject.AbstractFacetLoader;
import net.sunken.common.inject.Facet;
import org.bukkit.event.*;
import org.bukkit.plugin.java.*;

import java.util.Set;

@Log
@Singleton
public class PluginFacetLoader extends AbstractFacetLoader {

    private final JavaPlugin plugin;
    private final EventManager eventManager;
    private final BaseCommandRegistry baseCommandRegistry;

    @Inject
    public PluginFacetLoader(JavaPlugin plugin, EventManager eventManager, BaseCommandRegistry baseCommandRegistry, Set<Facet> pluginFacets) {
        super(pluginFacets);
        this.plugin = plugin;
        this.eventManager = eventManager;
        this.baseCommandRegistry = baseCommandRegistry;
    }

    @Override
    public void start() {
        registerCommands();
        registerListeners();
        registerSunkenListeners();
        super.enableAllFacets();
    }

    @Override
    public void stop() {
        super.disableAllFacets();
    }

    private void registerListeners() {
        super.find(Listener.class).forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
    }

    private void registerSunkenListeners() {
        super.find(SunkenListener.class).forEach(listener -> eventManager.register(listener));
    }

    private void registerCommands() {
       // super.find(BukkitCommand.class).forEach(command -> plugin.getCommand(command.getCommandName()).setExecutor(command));
        super.find(BaseCommand.class).forEach(baseCommand -> baseCommandRegistry.register(baseCommand));
    }

}
