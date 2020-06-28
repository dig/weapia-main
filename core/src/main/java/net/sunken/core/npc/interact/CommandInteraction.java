package net.sunken.core.npc.interact;

import lombok.AllArgsConstructor;
import net.minecraft.server.v1_15_R1.EnumHand;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import net.sunken.core.executor.BukkitSyncExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class CommandInteraction implements NPCInteraction {

    private final String command;
    private final BukkitSyncExecutor bukkitSyncExecutor;

    @Override
    public void onInteract(Player player, PacketPlayInUseEntity.EnumEntityUseAction enumEntityUseAction, EnumHand enumHand) {
        if ((enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK || enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT)
                && enumHand == EnumHand.MAIN_HAND) {
            bukkitSyncExecutor.execute(() -> Bukkit.dispatchCommand(player, command));
        }
    }
}
