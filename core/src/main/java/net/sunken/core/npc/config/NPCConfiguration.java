package net.sunken.core.npc.config;

import lombok.Getter;
import net.sunken.core.config.LocationConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class NPCConfiguration {

    @Setting
    private String id;

    @Setting
    private List<String> displayName;

    @Setting("skin")
    private SkinConfiguration skinConfiguration;

    @Setting("location")
    private LocationConfiguration locationConfiguration;

    @Setting("server")
    private NPCServerConfiguration serverConfiguration;

    @Setting("interaction")
    private InteractionConfiguration interactionConfiguration;

}
