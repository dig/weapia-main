package net.sunken.common.packet;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import net.sunken.common.inject.FacetBinder;

public class PacketModule extends AbstractModule {

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("PacketChannel")).to("SUNKEN_PACKET_CHANNEL");

        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(PacketHandlerRegistry.class);
        facetBinder.addBinding(PacketListener.class);
    }
}
