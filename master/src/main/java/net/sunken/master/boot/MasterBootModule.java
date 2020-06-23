package net.sunken.master.boot;

import com.google.inject.*;
import net.sunken.common.inject.*;

public class MasterBootModule extends AbstractModule {

    @Override
    public void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(MasterBootNotifier.class);
    }
}
