package net.sunken.master.party;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.PluginFacetBinder;

public class PartyModule extends AbstractModule {

    @Override
    public void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(PartyManager.class);
    }
}
