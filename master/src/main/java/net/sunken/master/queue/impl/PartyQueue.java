package net.sunken.master.queue.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.sunken.master.party.Party;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PartyQueue implements IQueue {

    private Party party;

    @Override
    public List<UUID> getMembers() {
        return party.getMembersAsUuid();
    }

}
