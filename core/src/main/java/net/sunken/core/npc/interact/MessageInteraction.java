package net.sunken.core.npc.interact;

import lombok.AllArgsConstructor;
import net.minecraft.server.v1_15_R1.EnumHand;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public class MessageInteraction implements NPCInteraction {

    private final List<String> message;

    @Override
    public void onInteract(Player player, PacketPlayInUseEntity.EnumEntityUseAction enumEntityUseAction, EnumHand enumHand) {
        if ((enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK || enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT)
                && enumHand == EnumHand.MAIN_HAND) {
            message.forEach(message -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
        }
    }
}
