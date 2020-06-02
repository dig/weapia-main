package net.sunken.core.npc.interact;

import lombok.AllArgsConstructor;
import net.minecraft.server.v1_15_R1.EnumHand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public class MessageInteraction implements NPCInteraction {

    private List<String> message;

    @Override
    public void onInteract(Player player, EnumHand enumHand) {
        message.forEach(message -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

}
