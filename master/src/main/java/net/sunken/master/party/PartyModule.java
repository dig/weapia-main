package net.sunken.master.party;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.FacetBinder;

public class PartyModule extends AbstractModule {

    @Override
    public void configure() {
        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(PartyManager.class);
    }
}
