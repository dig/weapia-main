package net.sunken.bungeecord.chat.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@Getter
@ConfigSerializable
public class ChatConfiguration {

    @Setting
    private Long delayBetweenMessages;
    @Setting
    private List<String> blacklistedWords;
    @Setting
    private List<String> exactBlacklistedWords;

}
