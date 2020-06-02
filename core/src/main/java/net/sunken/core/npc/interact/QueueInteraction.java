package net.sunken.core.npc.interact;

import lombok.AllArgsConstructor;
import net.minecraft.server.v1_15_R1.EnumHand;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.core.Constants;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class QueueInteraction implements NPCInteraction {

    private Server.Type type;
    private Game game;
    private boolean save;
    private PacketUtil packetUtil;

    @Override
    public void onInteract(Player player, EnumHand enumHand) {
        player.sendMessage(String.format(Constants.SEND_TO_GAME, game.getFriendlyName()));
        packetUtil.send(new PlayerRequestServerPacket(player.getUniqueId(), type, game, save));
    }

}
