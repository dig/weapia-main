package net.sunken.lobby.inventory;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.PluginFacetBinder;

public class InventoryModule extends AbstractModule {

    @Override
    protected void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(GameSelectorHandler.class);
        pluginFacetBinder.addBinding(LobbySelectorHandler.class);
    }

}
