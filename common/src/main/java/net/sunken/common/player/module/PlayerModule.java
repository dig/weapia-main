package net.sunken.common.player.module;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.PluginFacetBinder;

public class PlayerModule extends AbstractModule {

    @Override
    public void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(PlayerManager.class);
    }

}
