package net.sunken.master.party;

import com.google.common.cache.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.inject.Disableable;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.party.packet.*;
import net.sunken.common.player.PlayerDetail;
import net.sunken.common.player.Rank;
import net.sunken.common.util.DummyObject;
import net.sunken.common.util.RedisUtil;
import net.sunken.master.party.handler.*;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Singleton
public class PartyManager implements Facet, Enableable, Disableable {

    @Inject
    private RedisConnection redisConnection;

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private PartyCreateHandler partyCreateHandler;
    @Inject
    private PartyDisbandHandler partyDisbandHandler;
    @Inject
    private PartyInviteHandler partyInviteHandler;
    @Inject
    private PartyInviteFinishHandler partyInviteFinishHandler;
    @Inject
    private PartyMessageRequestHandler partyMessageRequestHandler;
    @Inject
    private PartyLeaveHandler partyLeaveHandler;
    @Inject
    private PartySetLeaderHandler partySetLeaderHandler;
    @Inject
    private PartyKickHandler partyKickHandler;

    private Map<UUID, Party> parties = Maps.newConcurrentMap();
    private Cache<PartyInvite, DummyObject> partyPendingInvites;

    public PartyManager() {
        partyPendingInvites = CacheBuilder.newBuilder()
                .expireAfterWrite(20, TimeUnit.SECONDS)
                .removalListener((RemovalListener<PartyInvite, DummyObject>) notification -> {
                    if (notification.getCause() == RemovalCause.EXPIRED) {
                        log.info(String.format("Invite expired for %s.", notification.getKey().getTarget().toString()));
                    }
                })
                .build();
    }

    @Override
    public void enable() {
        try (Jedis jedis = redisConnection.getConnection()) {
            Set<String> keys = RedisUtil.scanAll(jedis, PartyHelper.PARTY_STORAGE_KEY + ":*");
            for (String key : keys) {
                Map<String, String> kv = jedis.hgetAll(key);
                try {
                    Party party = fromRedis(kv);
                    Set<String> membersKeys = RedisUtil.scanAll(jedis, PartyHelper.PARTY_MEMBERS_STORAGE_KEY + ":" + party.getUuid().toString() + ":*");

                    for (String memberKey : membersKeys) {
                        Map<String, String> memberKV = jedis.hgetAll(memberKey);
                        PlayerDetail playerDetail = fromRedisMember(memberKV);
                        party.getMembers().add(playerDetail);
                    }

                    parties.put(party.getUuid(), party);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Unable to load party", e);
                }
            }
        }

        packetHandlerRegistry.registerHandler(PartyCreatePacket.class, partyCreateHandler);
        packetHandlerRegistry.registerHandler(PartyDisbandPacket.class, partyDisbandHandler);
        packetHandlerRegistry.registerHandler(PartyInvitePacket.class, partyInviteHandler);
        packetHandlerRegistry.registerHandler(PartyInviteFinishPacket.class, partyInviteFinishHandler);
        packetHandlerRegistry.registerHandler(PartyMessageRequestPacket.class, partyMessageRequestHandler);
        packetHandlerRegistry.registerHandler(PartyLeavePacket.class, partyLeaveHandler);
        packetHandlerRegistry.registerHandler(PartySetLeaderPacket.class, partySetLeaderHandler);
        packetHandlerRegistry.registerHandler(PartyKickPacket.class, partyKickHandler);
    }

    @Override
    public void disable() {
        try (Jedis jedis = redisConnection.getConnection()) {
            Set<String> keys = RedisUtil.scanAll(jedis, PartyHelper.PARTY_STORAGE_KEY + ":*");
            Set<String> memberKeys = RedisUtil.scanAll(jedis, PartyHelper.PARTY_MEMBERS_STORAGE_KEY + ":*");

            if (keys.size() > 0) jedis.del(keys.toArray(new String[keys.size()]));
            if (memberKeys.size() > 0) jedis.del(memberKeys.toArray(new String[memberKeys.size()]));

            for (Party party : parties.values()) {
                ImmutableMap.Builder<String, String> partyDataBuilder = ImmutableMap.<String, String>builder()
                        .put(PartyHelper.PARTY_UUID_KEY, party.getUuid().toString())
                        .put(PartyHelper.PARTY_CREATED_KEY, party.getCreated().toString())
                        .put(PartyHelper.PARTY_LEADER_UUID_KEY, party.getLeaderUUID().toString());
                jedis.hmset(PartyHelper.PARTY_STORAGE_KEY + ":" + party.getUuid().toString(), partyDataBuilder.build());

                for (PlayerDetail playerDetail : party.getMembers()) {
                    ImmutableMap.Builder<String, String> partyMemberBuilder = ImmutableMap.<String, String>builder()
                            .put(PartyHelper.PARTY_MEMBERS_UUID_KEY, playerDetail.getUuid().toString())
                            .put(PartyHelper.PARTY_MEMBERS_DISPLAYNAME_KEY, playerDetail.getDisplayName())
                            .put(PartyHelper.PARTY_MEMBERS_RANK_KEY, playerDetail.getRank().toString());

                    jedis.hmset(PartyHelper.PARTY_MEMBERS_STORAGE_KEY + ":" + party.getUuid().toString() + ":" + playerDetail.getUuid().toString(),
                            partyMemberBuilder.build());
                }
            }
        }
    }

    private Party fromRedis(Map<String, String> kv) {
        UUID uuid = UUID.fromString(kv.get(PartyHelper.PARTY_UUID_KEY));
        Long created = Long.parseLong(kv.get(PartyHelper.PARTY_CREATED_KEY));
        UUID leaderUUID = UUID.fromString(kv.get(PartyHelper.PARTY_LEADER_UUID_KEY));

        return Party.builder()
                .uuid(uuid)
                .created(created)
                .leaderUUID(leaderUUID)
                .members(new LinkedHashSet<>())
                .build();
    }

    private PlayerDetail fromRedisMember(Map<String, String> kv) {
        UUID uuid = UUID.fromString(kv.get(PartyHelper.PARTY_MEMBERS_UUID_KEY));
        String displayName = kv.get(PartyHelper.PARTY_MEMBERS_DISPLAYNAME_KEY);
        Rank rank = Rank.valueOf(kv.get(PartyHelper.PARTY_MEMBERS_RANK_KEY));

        return new PlayerDetail(uuid, displayName, rank);
    }

    public PartyCreateResponsePacket.PartyCreateStatus createParty(@NonNull PlayerDetail leader) {
        if (findPartyByMember(leader.getUuid()).isPresent()) {
            return PartyCreateResponsePacket.PartyCreateStatus.ALREADY_IN_PARTY;
        }

        Party party = new Party(leader);
        parties.put(party.getUuid(), party);

        return PartyCreateResponsePacket.PartyCreateStatus.SUCCESS;
    }

    public PartyInviteResponsePacket.PartyInviteStatus invite(@NonNull Party party, @NonNull UUID target) {
        partyPendingInvites.put(new PartyInvite(target, party.getUuid()), new DummyObject());
        return PartyInviteResponsePacket.PartyInviteStatus.SUCCESS;
    }

    public boolean isInvitedToParty(@NonNull Party party, @NonNull UUID target) {
        return partyPendingInvites.asMap().keySet().stream()
                .filter(partyInvite -> partyInvite.getTarget().equals(target))
                .filter(partyInvite -> partyInvite.getParty().equals(party.getUuid()))
                .count() > 0;
    }

    public List<PartyInvite> getInvites(@NonNull UUID uuid) {
        return partyPendingInvites.asMap().keySet().stream()
                .filter(partyInvite -> partyInvite.getTarget().equals(uuid))
                .collect(Collectors.toList());
    }

    public Optional<Party> getInvite(@NonNull UUID uuid, @NonNull String leaderName) {
        for (PartyInvite partyInvite : getInvites(uuid)) {
            Optional<Party> partyOptional = findPartyByUUID(partyInvite.getParty());

            if (partyOptional.isPresent()) {
                Party party = partyOptional.get();

                if (party.getLeader().isPresent()) {
                    PlayerDetail partyLeader = party.getLeader().get();

                    if (partyLeader.getDisplayName().equalsIgnoreCase(leaderName)) {
                        return Optional.of(party);
                    }
                }
            }
        }

        return Optional.empty();
    }

    public boolean disband(@NonNull Party party) {
        for (PartyInvite partyInvite : partyPendingInvites.asMap().keySet()) {
            if (partyInvite.getParty().equals(party.getUuid())) {
                partyPendingInvites.invalidate(partyInvite);
            }
        }

        parties.remove(party.getUuid());
        return true;
    }

    public void accept(@NonNull Party party, @NonNull PlayerDetail target) {
        for (PartyInvite partyInvite : partyPendingInvites.asMap().keySet()) {
            if (partyInvite.getParty().equals(party.getUuid()) && partyInvite.getTarget().equals(target.getUuid())) {
                partyPendingInvites.invalidate(partyInvite);
            }
        }

        party.getMembers().add(target);
    }

    public void deny(@NonNull Party party, @NonNull UUID uuid) {
        for (PartyInvite partyInvite : partyPendingInvites.asMap().keySet()) {
            if (partyInvite.getParty().equals(party.getUuid()) && partyInvite.getTarget().equals(uuid)) {
                partyPendingInvites.invalidate(partyInvite);
            }
        }
    }

    public void leave(@NonNull Party party, @NonNull UUID uuid) {
        party.getMembers().removeIf(playerDetail -> playerDetail.getUuid().equals(uuid));
    }

    public void setLeader(@NonNull Party party, @NonNull UUID uuid) {
        party.setLeaderUUID(uuid);
    }

    public Optional<Party> findPartyByUUID(@NonNull UUID uuid) {
        return Optional.ofNullable(parties.get(uuid));
    }

    public Optional<Party> findPartyByMember(@NonNull UUID uuid) {
        for (Party party : parties.values()) {
            for (PlayerDetail playerDetail : party.getMembers()) {
                if (playerDetail.getUuid().equals(uuid)) return Optional.of(party);
            }
        }

        return Optional.empty();
    }

    public Optional<Party> findPartyByMember(@NonNull String displayName) {
        for (Party party : parties.values()) {
            for (PlayerDetail playerDetail : party.getMembers()) {
                if (playerDetail.getDisplayName().equals(displayName)) return Optional.of(party);
            }
        }

        return Optional.empty();
    }

    public Optional<PlayerDetail> findMemberInParty(@NonNull Party party, @NonNull UUID uuid) {
        return party.getMembers().stream()
                .filter(playerDetail -> playerDetail.getUuid().equals(uuid))
                .findFirst();
    }

    @Getter
    @AllArgsConstructor
    private class PartyInvite {
        private UUID target;
        private UUID party;
    }
}
