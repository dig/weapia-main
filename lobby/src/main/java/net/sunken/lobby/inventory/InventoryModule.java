package net.sunken.lobby.inventory;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.FacetBinder;

public class InventoryModule extends AbstractModule {

    @Override
    protected void configure() {
        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(GameSelectorItem.class);
        facetBinder.addBinding(LobbySelectorItem.class);
    }
}
