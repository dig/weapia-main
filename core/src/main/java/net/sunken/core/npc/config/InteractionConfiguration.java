package net.sunken.core.npc.config;

import lombok.Getter;
import net.sunken.core.npc.interact.NPCInteractionType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class InteractionConfiguration {

    @Setting
    private NPCInteractionType type;

    @Setting
    private List<String> values;

    @Setting
    private long cooldown;

}
