package net.sunken.common.player;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import lombok.NonNull;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.AbstractPlayer;

import java.util.*;

@Singleton
public class PlayerManager {

    private final Map<UUID, AbstractPlayer> playerCache = Maps.newConcurrentMap();
    private final Map<String, UUID> nameCache = Maps.newConcurrentMap();

    public void add(@NonNull AbstractPlayer abstractPlayer) {
        playerCache.put(abstractPlayer.getUuid(), abstractPlayer);
        nameCache.put(abstractPlayer.getUsername().toLowerCase(), abstractPlayer.getUuid());
    }

    public Optional<AbstractPlayer> get(@NonNull UUID uuid) {
        return Optional.ofNullable(playerCache.get(uuid));
    }

    public Optional<AbstractPlayer> get(@NonNull String name) {
        name = name.toLowerCase();
        if (nameCache.containsKey(name)) {
            return get(nameCache.get(name));
        }
        return Optional.empty();
    }

    public void remove(@NonNull UUID uuid) {
        playerCache.remove(uuid);
        nameCache.keySet().forEach(name -> {
            UUID value = nameCache.get(name);
            if (value.equals(uuid)) {
                nameCache.remove(name);
            }
        });
    }

    public Collection<AbstractPlayer> getOnlinePlayers() {
        return playerCache.values();
    }
}
