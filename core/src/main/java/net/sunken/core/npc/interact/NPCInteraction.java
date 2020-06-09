package net.sunken.core.npc.interact;

import net.minecraft.server.v1_15_R1.EnumHand;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

public interface NPCInteraction {

    void onInteract(Player player, PacketPlayInUseEntity.EnumEntityUseAction enumEntityUseAction, EnumHand enumHand);

}
