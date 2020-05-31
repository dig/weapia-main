package net.sunken.core.engine.state.impl;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public abstract class EventGameState extends BaseGameState {

    public abstract void onJoin(Player player);
    public abstract void onQuit(Player player);

    public abstract void onDeath(PlayerDeathEvent event);
    public abstract void onRespawn(PlayerRespawnEvent event);

    public abstract boolean canBreak(Player player, Block block);
    public abstract boolean canPlace(Player player, Block block);

    public abstract boolean canTakeDamage(Player target, Entity instigator, EntityDamageEvent.DamageCause damageCause);
    public abstract boolean canDealDamage(Player instigator, Entity target, EntityDamageEvent.DamageCause damageCause);

}
