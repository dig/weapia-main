package net.sunken.common.util.cooldown;

import com.google.common.cache.*;
import com.google.inject.*;
import lombok.*;

import java.util.*;
import java.util.concurrent.*;

@Singleton
public class Cooldowns {

    private Cache<CooldownId, Cooldown> cooldownCache;

    public Cooldowns() {
        cooldownCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5L, TimeUnit.MINUTES)
                .build();
    }

    public void create(@NonNull String key, @NonNull UUID uuid, long timeToExpireMs) {
        final CooldownId id = CooldownId.of(key, uuid);
        cooldownCache.put(id, new Cooldown(id, timeToExpireMs));
    }

    public boolean canProceed(@NonNull String key, @NonNull UUID uuid) {
        final Cooldown cooldown = cooldownCache.getIfPresent(CooldownId.of(key, uuid));
        if (cooldown != null) {
            return System.currentTimeMillis() >= cooldown.getTimeToExpire();
        }
        return true;
    }

}
