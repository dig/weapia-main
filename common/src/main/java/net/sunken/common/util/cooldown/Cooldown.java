package net.sunken.common.util.cooldown;

import lombok.*;

@Getter
@AllArgsConstructor
class Cooldown {

    private final CooldownId id;
    private final long timeToExpire;
}
