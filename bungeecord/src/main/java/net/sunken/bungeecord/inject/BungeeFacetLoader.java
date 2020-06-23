package net.sunken.bungeecord.inject;

import com.google.inject.Inject;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.sunken.common.command.impl.BaseCommand;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.inject.AbstractFacetLoader;
import net.sunken.common.inject.Facet;
import net.sunken.common.inject.annotation.PostInit;
import net.sunken.common.inject.annotation.PreInit;

import java.util.Set;

public class BungeeFacetLoader extends AbstractFacetLoader {

    private final Plugin plugin;
    private final BaseCommandRegistry baseCommandRegistry;

    @Inject
    public BungeeFacetLoader(Plugin plugin, BaseCommandRegistry baseCommandRegistry, Set<Facet> pluginFacets) {
        super(pluginFacets);
        this.plugin = plugin;
        this.baseCommandRegistry = baseCommandRegistry;
    }

    @Override
    public void start() {
        super.enableAllFacets(facet -> facet.getClass().isAnnotationPresent(PreInit.class));
        registerCommands();
        registerListeners();
        super.enableAllFacets(facet -> facet.getClass().isAnnotationPresent(PostInit.class)
                || !facet.getClass().isAnnotationPresent(PreInit.class));
    }

    @Override
    public void stop() {
        super.disableAllFacets();
    }

    private void registerListeners() {
        super.find(Listener.class).forEach(listener -> plugin.getProxy().getPluginManager().registerListener(plugin, listener));
    }

    private void registerCommands() {
        super.find(Command.class).forEach(command -> plugin.getProxy().getPluginManager().registerCommand(plugin, command));
        super.find(BaseCommand.class).forEach(baseCommand -> baseCommandRegistry.register(baseCommand));
    }
}