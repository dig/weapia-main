package net.sunken.master.queue;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.PluginFacetBinder;

public class QueueModule extends AbstractModule {

    @Override
    public void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(QueueManager.class);
    }
}
