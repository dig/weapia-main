package net.sunken.lobby.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class SelectorInteractionConfiguration {

    @Setting
    private Type type;
    @Setting
    private List<String> data;

    public enum Type {
        QUEUE,
        MESSAGE
    }

}
