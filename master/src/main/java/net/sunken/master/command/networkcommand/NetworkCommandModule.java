package net.sunken.master.command.networkcommand;

import com.google.inject.*;
import net.sunken.common.inject.*;

public class NetworkCommandModule extends AbstractModule {

    @Override
    public void configure() {
        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(AvailableCommandsRecorder.class);
        facetBinder.addBinding(NetworkCommandHandler.class);
    }

}
