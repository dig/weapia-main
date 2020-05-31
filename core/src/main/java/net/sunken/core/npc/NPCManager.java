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
import net.minecraft.server.v1_12_R1.EnumHand;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;
import net.sunken.common.event.EventManager;
import net.sunken.common.event.ListensToEvent;
import net.sunken.common.event.SunkenListener;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.util.cooldown.Cooldowns;
import net.sunken.core.Constants;
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.hologram.HologramFactory;
import net.sunken.core.npc.config.InteractionConfiguration;
import net.sunken.core.npc.event.NPCInteractEvent;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Log
@Singleton
public class NPCManager implements Facet, Enableable, Listener, SunkenListener {

    @Inject
    private EventManager eventManager;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private Cooldowns cooldown;
    @Inject
    private ProtocolManager protocolManager;
    @Inject
    private JavaPlugin javaPlugin;
    @Inject
    private BukkitSyncExecutor bukkitSyncExecutor;
    @Inject
    private HologramFactory hologramFactory;

    private Map<String, NPC> npcMap;

    public NPCManager() {
        npcMap = new HashMap<>();
    }

    public NPC create(@NonNull String key, @NonNull String displayName, @NonNull Location location, @NonNull String texture, @NonNull String signature) {
        NPC npc = new NPC(displayName, location, texture, signature, bukkitSyncExecutor, hologramFactory);
        npcMap.put(key, npc);

        return npc;
    }

    public NPC create(@NonNull String key, @NonNull List<String> displayName, @NonNull Location location, @NonNull String texture, @NonNull String signature) {
        NPC npc = new NPC(displayName, location, texture, signature, bukkitSyncExecutor, hologramFactory);
        npcMap.put(key, npc);

        return npc;
    }

    public NPC create(@NonNull String key, @NonNull String displayName, @NonNull Location location, @NonNull String texture, @NonNull String signature, boolean showAll) {
        NPC npc = create(key, displayName, location, texture, signature);
        if (showAll) npc.showAll();

        return npc;
    }

    public NPC create(@NonNull String key, @NonNull List<String> displayName, @NonNull Location location, @NonNull String texture, @NonNull String signature, boolean showAll) {
        NPC npc = create(key, displayName, location, texture, signature);
        if (showAll) npc.showAll();

        return npc;
    }

    public NPC create(@NonNull String key, @NonNull String displayName, @NonNull Location location, boolean showAll) {
        NPC npc = create(key, displayName, location, "", "");
        if (showAll) npc.showAll();

        return npc;
    }

    public NPC create(@NonNull String key, @NonNull List<String> displayName, @NonNull Location location, boolean showAll) {
        NPC npc = create(key, displayName, location, "", "");
        if (showAll) npc.showAll();

        return npc;
    }

    public void remove(@NonNull String key) {
        if (npcMap.containsKey(key)) {
            NPC npc = npcMap.get(key);
            npc.remove();

            npcMap.remove(key);
        }
    }

    public NPC get(String key) {
        return npcMap.get(key);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        npcMap.values().forEach(npc -> {
            if (npc.isShownToAll())
                npc.show(event.getPlayer());
        });

        log.info("Showing all NPCs to new player.");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        npcMap.values().forEach(npc -> npc.getViewers().remove(uuid));
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        npcMap.values().forEach(npc -> {
            CraftPlayer craftPlayer = npc.getBukkitEntity();

            if (craftPlayer.getWorld().getName().equals(event.getWorld().getName())
                && craftPlayer.getLocation().getChunk().getX() == chunk.getX()
                && craftPlayer.getLocation().getChunk().getZ() == chunk.getZ()) {
                npc.showAll();

                log.info(String.format("onChunkLoad: showAll() (%s)", npc.getDisplayName()));
            }
        });
    }

    @ListensToEvent
    public void onNPCInteract(NPCInteractEvent event) {
        NPC npc = event.getNpc();
        Player instigator = event.getInstigator();

        if ((event.getEnumEntityUseAction() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT ||
                event.getEnumEntityUseAction() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)
                && event.getEnumHand() == EnumHand.MAIN_HAND
                && npc.getInteraction() != null) {
            InteractionConfiguration interaction = npc.getInteraction();
            List<String> values = interaction.getValues();

            if (cooldown.canProceed(npc.getUniqueID().toString(), instigator.getUniqueId())) {
                switch (interaction.getType()) {
                    case MESSAGE:
                        values.forEach(message -> instigator.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
                        break;
                    case QUEUE:
                        Server.Type type = Server.Type.valueOf(values.get(0));
                        Game game = Game.valueOf(values.get(1));
                        boolean save = Boolean.valueOf(values.get(2));

                        instigator.sendMessage(String.format(Constants.SEND_TO_GAME, game.getFriendlyName()));
                        packetUtil.send(new PlayerRequestServerPacket(instigator.getUniqueId(), type, game, save));
                        break;
                }

                cooldown.create(npc.getUniqueID().toString(), instigator.getUniqueId(), System.currentTimeMillis() + interaction.getCooldown());
            } else {
                instigator.sendMessage(Constants.INTERACTION_COOLDOWN);
            }
        }
    }

    @Override
    public void enable() {
        //--- https://wiki.vg/Protocol#Use_Entity
        protocolManager.addPacketListener(
            new PacketAdapter(javaPlugin, ListenerPriority.NORMAL,
                    PacketType.Play.Client.USE_ENTITY) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    PacketContainer packet = event.getPacket();

                    if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                        int entityId = packet.getIntegers().read(0);
                        PacketPlayInUseEntity.EnumEntityUseAction enumEntityUseAction = PacketPlayInUseEntity.EnumEntityUseAction.valueOf(packet.getEntityUseActions().read(0).toString());

                        //--- Optional
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
        for (NPC npc : npcMap.values())
            npc.remove();
    }

}
