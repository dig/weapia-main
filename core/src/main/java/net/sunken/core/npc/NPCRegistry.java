package net.sunken.core.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.minecraft.server.v1_15_R1.EnumHand;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import net.sunken.common.event.EventManager;
import net.sunken.common.event.ListensToEvent;
import net.sunken.common.event.SunkenListener;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.core.Constants;
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.hologram.HologramFactory;
import net.sunken.core.npc.config.InteractionConfiguration;
import net.sunken.core.npc.event.NPCInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log
@Singleton
public class NPCRegistry implements Facet, Enableable, Listener {

    @Inject
    private ProtocolManager protocolManager;
    @Inject
    private JavaPlugin javaPlugin;
    @Inject
    private EventManager eventManager;
    @Inject
    private BukkitSyncExecutor bukkitSyncExecutor;
    @Inject
    private HologramFactory hologramFactory;

    private final Map<String, NPC> npcMap = new HashMap<>();

    public NPC register(@NonNull String key, @NonNull String displayName, @NonNull Location location, @NonNull String texture, @NonNull String signature) {
        NPC npc = new NPC(displayName, location, texture, signature, bukkitSyncExecutor, hologramFactory);
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

    @Override
    public void enable() {
        // https://wiki.vg/Protocol#Use_Entity
        protocolManager.addPacketListener(
                new PacketAdapter(javaPlugin, ListenerPriority.NORMAL,
                        PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        PacketContainer packet = event.getPacket();

                        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                            int entityId = packet.getIntegers().read(0);
                            PacketPlayInUseEntity.EnumEntityUseAction enumEntityUseAction = PacketPlayInUseEntity.EnumEntityUseAction.valueOf(packet.getEntityUseActions().read(0).toString());

                            EnumHand enumHand = EnumHand.MAIN_HAND;
                            if (enumEntityUseAction != PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)
                                enumHand = EnumHand.valueOf(packet.getHands().read(0).toString());

                            Optional<NPC> npcOptional = npcMap.values().stream()
                                    .filter(npc -> npc.getId() == entityId)
                                    .findFirst();

                            if (npcOptional.isPresent())
                                eventManager.callEvent(new NPCInteractEvent(npcOptional.get(), event.getPlayer(), enumEntityUseAction, enumHand));
                        }
                    }
                });
    }

    @Override
    public void disable() {
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        npcMap.values().forEach(npc -> npc.show(event.getPlayer()));
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        npcMap.values().forEach(npc -> {
            CraftPlayer craftPlayer = npc.getBukkitEntity();

            if (craftPlayer.getWorld().getName().equals(event.getWorld().getName())
                    && craftPlayer.getLocation().getChunk().getX() == chunk.getX()
                    && craftPlayer.getLocation().getChunk().getZ() == chunk.getZ()) {

                Bukkit.getOnlinePlayers().forEach(player -> npc.show(player));
                log.info(String.format("onChunkLoad: showing to all. (%s)", npc.getDisplayName()));
            }
        });
    }

}
