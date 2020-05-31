package net.sunken.master.kube;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@ConfigSerializable
public class KubeConfiguration {

    @Setting
    private String branch;

}
