package net.sunken.master.inject;

import com.google.inject.Inject;
import net.sunken.common.inject.AbstractFacetLoader;
import net.sunken.common.inject.Facet;

import java.util.Set;

public class MasterFacetLoader extends AbstractFacetLoader {

    @Inject
    public MasterFacetLoader(Set<Facet> pluginFacets) {
        super(pluginFacets);
    }

    @Override
    public void start() {
        super.enableAllFacets();
    }

    @Override
    public void stop() {
        super.disableAllFacets();
    }
}
