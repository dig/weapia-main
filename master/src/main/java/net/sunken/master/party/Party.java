package net.sunken.master.party;

import lombok.*;
import net.sunken.common.player.PlayerDetail;

import java.util.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class Party {

    private UUID uuid;
    private Long created;

    @Setter
    private UUID leaderUUID;
    private Set<PlayerDetail> members;

    public Party(PlayerDetail leader) {
        this.uuid = UUID.randomUUID();
        this.created = System.currentTimeMillis();
        this.leaderUUID = leader.getUuid();
        this.members = new LinkedHashSet<>();

        this.members.add(leader);
    }

    public Optional<PlayerDetail> getLeader() {
        return members.stream()
                .filter(playerDetail -> playerDetail.getUuid().equals(leaderUUID))
                .findFirst();
    }

    public List<UUID> getMembersAsUuid() {
        List<UUID> result = new ArrayList<>();

        for (PlayerDetail playerDetail : members)
            result.add(playerDetail.getUuid());

        return result;
    }
}
