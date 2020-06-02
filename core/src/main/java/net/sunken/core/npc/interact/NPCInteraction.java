package net.sunken.core.npc.interact;

import net.minecraft.server.v1_15_R1.EnumHand;
import org.bukkit.entity.Player;

public interface NPCInteraction {

    void onInteract(Player player, EnumHand enumHand);

}
