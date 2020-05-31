package net.sunken.core.npc.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.EnumHand;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;
import net.sunken.common.event.SunkenEvent;
import net.sunken.core.npc.NPC;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class NPCInteractEvent extends SunkenEvent {

    private NPC npc;
    private Player instigator;
    private PacketPlayInUseEntity.EnumEntityUseAction enumEntityUseAction;
    private EnumHand enumHand;

}
