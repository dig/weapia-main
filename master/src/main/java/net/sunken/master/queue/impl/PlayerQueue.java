package net.sunken.master.queue.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PlayerQueue implements IQueue {

    private UUID uuid;

    @Override
    public List<UUID> getMembers() {
        return Arrays.asList(uuid);
    }

}
