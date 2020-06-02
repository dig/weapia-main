package net.sunken.core.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_15_R1.EnumHand;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import net.sunken.core.npc.interact.NPCInteraction;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class NPCPacketAdapter extends PacketAdapter {

    private NPCRegistry npcRegistry;

    public NPCPacketAdapter(Plugin plugin, NPCRegistry npcRegistry, ListenerPriority listenerPriority, PacketType... types) {
        super(plugin, listenerPriority, types);
        this.npcRegistry = npcRegistry;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
            int entityId = packet.getIntegers().read(0);
            PacketPlayInUseEntity.EnumEntityUseAction enumEntityUseAction = PacketPlayInUseEntity.EnumEntityUseAction.valueOf(packet.getEntityUseActions().read(0).toString());

            EnumHand enumHand = EnumHand.MAIN_HAND;
            if (enumEntityUseAction != PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)
                enumHand = EnumHand.valueOf(packet.getHands().read(0).toString());

            Optional<NPC> npcOptional = npcRegistry.getNPCs().stream()
                    .filter(npc -> npc.getId() == entityId)
                    .findFirst();

            if (npcOptional.isPresent()) {
                NPC npc = npcOptional.get();
                NPCInteraction npcInteraction = npc.getInteraction();
                if (npcInteraction != null) {
                    npcInteraction.onInteract(event.getPlayer(), enumHand);
                }
            }
        }
    }

}
