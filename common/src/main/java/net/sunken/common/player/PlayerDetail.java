package net.sunken.common.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class PlayerDetail implements Serializable  {

    private final UUID uuid;
    private final String displayName;
    private final Rank rank;

}
