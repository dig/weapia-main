package net.sunken.core.npc.interact;

import lombok.AllArgsConstructor;
import net.minecraft.server.v1_15_R1.EnumHand;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.Constants;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class QueueInteraction implements NPCInteraction {

    private final Server.Type type;
    private final Game game;
    private final boolean save;
    private final PacketUtil packetUtil;

    @Override
    public void onInteract(Player player, PacketPlayInUseEntity.EnumEntityUseAction enumEntityUseAction, EnumHand enumHand) {
        if ((enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK || enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT)
                && enumHand == EnumHand.MAIN_HAND) {
            player.sendMessage(String.format(Constants.SEND_TO_GAME, game.getFriendlyName()));
            AsyncHelper.executor().submit(() -> packetUtil.send(new PlayerRequestServerPacket(player.getUniqueId(), type, game, save)));
        }
    }
}
