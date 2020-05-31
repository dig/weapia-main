package net.sunken.core.inventory.element;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import lombok.Getter;

import java.util.UUID;

@Singleton
public class ElementRegistry {

    @Getter
    private final Cache<UUID, Element> registry;

    public ElementRegistry() {
        registry = CacheBuilder.newBuilder().
                build();
    }

}
