package net.sunken.master.reboot;

import com.google.inject.*;
import net.sunken.common.inject.*;

public class MasterRebootModule extends AbstractModule {

    @Override
    public void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(MasterRebootNotifier.class);
    }

}
