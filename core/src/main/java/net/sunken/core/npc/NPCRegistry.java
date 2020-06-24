package net.sunken.core.npc;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.inject.Disableable;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.hologram.HologramFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.*;

@Log
@Singleton
public class NPCRegistry implements Facet, Disableable, Listener {

    @Inject
    private BukkitSyncExecutor bukkitSyncExecutor;
    @Inject
    private HologramFactory hologramFactory;

    private final Map<String, NPC> npcMap = new HashMap<>();

    public NPC register(@NonNull String key, @NonNull String displayName, @NonNull Location location, @NonNull String texture, @NonNull String signature) {
        NPC npc = new NPC(displayName, location, texture, signature, bukkitSyncExecutor);
        npcMap.put(key, npc);
        return npc;
    }

    public NPC register(@NonNull String key, @NonNull List<String> displayName, @NonNull Location location, @NonNull String texture, @NonNull String signature) {
        NPC npc = new NPC(displayName, location, texture, signature, bukkitSyncExecutor, hologramFactory);
        npcMap.put(key, npc);
        return npc;
    }

    public NPC register(@NonNull String key, @NonNull String displayName, @NonNull Location location) {
        return register(key, displayName, location, "", "");
    }

    public NPC register(@NonNull String key, @NonNull List<String> displayName, @NonNull Location location) {
        return register(key, displayName, location, "", "");
    }

    public void unregister(@NonNull String key) {
        if (npcMap.containsKey(key)) {
            NPC npc = npcMap.get(key);
            npc.remove();
        }
    }

    public NPC get(@NonNull String key) {
        return npcMap.get(key);
    }

    public Collection<NPC> getNPCs() {
        return npcMap.values();
    }

    @Override
    public void disable() {
        npcMap.values().forEach(NPC::remove);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        npcMap.values().forEach(npc -> npc.show(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getOnlinePlayers().forEach(this::show);
    }

    private void show(@NonNull Player player) {
        Location location = player.getLocation();
        npcMap.values().forEach(npc -> {
            CraftPlayer craftPlayer = npc.getBukkitEntity();
            if (craftPlayer.getWorld().equals(location.getWorld())
                    && craftPlayer.getLocation().getChunk().getX() == location.getChunk().getX()
                    && craftPlayer.getLocation().getChunk().getZ() == location.getChunk().getZ()) {
                npc.show(player);
            }
        });
    }
}
