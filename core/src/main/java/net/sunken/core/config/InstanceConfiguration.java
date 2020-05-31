package net.sunken.core.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.World;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class InstanceConfiguration {

    private String id;
    private Server.Type type;
    private Game game;
    private World world;

    private Optional<String> metadataId;

}
