package net.sunken.common.packet;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import net.sunken.common.inject.PluginFacetBinder;

public class PacketModule extends AbstractModule {

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("PacketChannel")).to("SUNKEN_PACKET_CHANNEL");

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(PacketHandlerRegistry.class);
        pluginFacetBinder.addBinding(PacketListener.class);
    }
}
