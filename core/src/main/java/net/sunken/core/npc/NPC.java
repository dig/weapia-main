package net.sunken.core.npc;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import net.minecraft.server.v1_15_R1.*;
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.hologram.Hologram;
import net.sunken.core.hologram.HologramFactory;
import net.sunken.core.npc.config.InteractionConfiguration;
import net.sunken.core.util.MojangUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

@Log
public class NPC extends EntityPlayer {

    @Getter
    private String displayPluginName;
    @Getter
    private boolean shownToAll;
    @Getter
    private List<UUID> viewers;
    @Getter
    private Hologram hologram;

    @Getter @Setter
    private InteractionConfiguration interaction;

    private BukkitSyncExecutor bukkitSyncExecutor;
    private HologramFactory hologramFactory;

    public NPC(@NonNull String displayName, @NonNull Location location, @NonNull String texture, @NonNull String signature, BukkitSyncExecutor bukkitSyncExecutor, HologramFactory hologramFactory) {
        super(((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) location.getWorld()).getHandle(),
                MojangUtil.toGameProfile(displayName, texture, signature),
                new PlayerInteractManager(((CraftWorld) location.getWorld()).getHandle()));

        this.displayPluginName = displayName;
        this.shownToAll = false;
        this.viewers = new ArrayList<>();
        this.hologram = null;

        this.bukkitSyncExecutor = bukkitSyncExecutor;
        this.hologramFactory = hologramFactory;

        setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        getBukkitEntity().setGameMode(GameMode.CREATIVE);
        getBukkitEntity().setRemoveWhenFarAway(false);

        getDataWatcher().set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);
    }

    public NPC(@NonNull List<String> displayName, @NonNull Location location, @NonNull String texture, @NonNull String signature, @NonNull BukkitSyncExecutor bukkitSyncExecutor, HologramFactory hologramFactory) {
        this(displayName.get(displayName.size() - 1), location, texture, signature, bukkitSyncExecutor, hologramFactory);
        displayName.remove(displayName.size() - 1);

        this.hologram = hologramFactory.createHologram(location.clone().add(0, 0.82 + (displayName.size() * 0.28), 0), displayName);
    }

    public void show(@NonNull Player player) {
        if (!viewers.contains(player.getUniqueId())) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(this));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(this, (byte) ((this.yaw * 256.0F) / 360.0F)));
            connection.sendPacket(new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true));

            bukkitSyncExecutor.execute(() -> connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this)), 6 * 20L);

            viewers.add(player.getUniqueId());
        } else {
            log.severe("NPC: Cannot show entity to player, already spawned on their client.");
        }
    }

    public void hide(@NonNull Player player) {
        if (viewers.contains(player.getUniqueId())) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityDestroy(this.getId()));

            viewers.remove(player.getUniqueId());
        } else {
            log.severe("NPC: Cannot hide entity from player, non existent.");
        }
    }

    public void showAll() {
        shownToAll = true;
        Bukkit.getOnlinePlayers().forEach(player -> show(player));
    }

    public void hideAll() {
        shownToAll = false;
        Bukkit.getOnlinePlayers().forEach(player -> hide(player));
    }

    public void remove() {
        hideAll();
        getBukkitEntity().remove();
        if (hologram != null) hologram.remove();
    }

    public enum Type {
        MESSAGE,
        QUEUE
    }

}
