package net.sunken.common.player.module;

import com.google.inject.Inject;
import com.mongodb.bulk.UpdateRequest;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.java.Log;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.database.MongoConnection;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.packet.PlayerSaveStatePacket;
import net.sunken.common.util.AsyncHelper;
import org.bson.Document;

import java.util.Optional;
import java.util.logging.Level;

import static com.mongodb.client.model.Filters.eq;

@Log
public class PlayerSaveStateHandler extends PacketHandler<PlayerSaveStatePacket> {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private MongoConnection mongoConnection;

    @Override
    public void onReceive(PlayerSaveStatePacket packet) {
        if (packet.getReason() == PlayerSaveStatePacket.Reason.REQUEST) {
            log.info(String.format("PlayerSaveStateHandler for %s", packet.getUuid()));

            playerManager.get(packet.getUuid()).ifPresent(abstractPlayer ->
                AsyncHelper.executor().execute(() -> {
                    boolean success = true;
                    if (!abstractPlayer.isSaved()) {
                        try {
                            MongoCollection<Document> collection = mongoConnection.getCollection(DatabaseHelper.DATABASE_MAIN, DatabaseHelper.COLLECTION_PLAYER);
                            collection.updateOne(eq(DatabaseHelper.PLAYER_UUID_KEY, abstractPlayer.getUuid().toString()),
                                    new Document("$set", abstractPlayer.toDocument()), new UpdateOptions().upsert(true));
                            abstractPlayer.setSaved(true);
                        } catch (Exception e) {
                            log.log(Level.SEVERE, "Failed to save player data.", e);
                            success = false;
                        }
                    }
                    packetUtil.send(new PlayerSaveStatePacket(abstractPlayer.getUuid(), success ? PlayerSaveStatePacket.Reason.COMPLETE : PlayerSaveStatePacket.Reason.FAIL));
                })
            );
        }
    }

}
