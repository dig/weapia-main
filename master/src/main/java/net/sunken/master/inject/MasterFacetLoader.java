package net.sunken.master.inject;

import com.google.inject.Inject;
import net.sunken.common.command.impl.BaseCommand;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.inject.AbstractFacetLoader;
import net.sunken.common.inject.Facet;
import net.sunken.common.inject.annotation.PostInit;
import net.sunken.common.inject.annotation.PreInit;

import java.util.Set;

public class MasterFacetLoader extends AbstractFacetLoader {

    private final BaseCommandRegistry baseCommandRegistry;

    @Inject
    public MasterFacetLoader(BaseCommandRegistry baseCommandRegistry, Set<Facet> facets) {
        super(facets);
        this.baseCommandRegistry = baseCommandRegistry;
    }

    @Override
    public void start() {
        super.enableAllFacets(facet -> facet.getClass().isAnnotationPresent(PreInit.class));
        registerCommands();
        super.enableAllFacets(facet -> facet.getClass().isAnnotationPresent(PostInit.class)
                || !facet.getClass().isAnnotationPresent(PreInit.class));
    }

    @Override
    public void stop() {
        super.disableAllFacets();
    }

    private void registerCommands() {
        super.find(BaseCommand.class).forEach(baseCommand -> baseCommandRegistry.register(baseCommand));
    }
}
