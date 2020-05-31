package net.sunken.core.bar.module;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.core.bar.BarSettings;
import net.sunken.core.bar.command.BarCommand;

public class BarModule extends AbstractModule {

    @Override
    protected void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(BarSettings.class);
        pluginFacetBinder.addBinding(BarCommand.class);
    }

}
