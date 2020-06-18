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
            Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(packet.getUuid());

            if (abstractPlayerOptional.isPresent()) {
                AbstractPlayer abstractPlayer = abstractPlayerOptional.get();

                log.info(String.format("PlayerSaveStateHandler: Saving player (%s)", abstractPlayer.getUuid().toString()));
                AsyncHelper.executor().submit(() -> {
                    MongoCollection<Document> collection = mongoConnection.getCollection(DatabaseHelper.DATABASE_MAIN, DatabaseHelper.COLLECTION_PLAYER);
                    UpdateResult result = collection.updateOne(eq("uuid", abstractPlayer.getUuid().toString()),
                            new Document("$set", abstractPlayer.toDocument()), new UpdateOptions().upsert(true));

                    packetUtil.send(new PlayerSaveStatePacket(abstractPlayer.getUuid(), (result.wasAcknowledged() ? PlayerSaveStatePacket.Reason.COMPLETE : PlayerSaveStatePacket.Reason.FAIL)));
                    log.info(String.format("PlayerSaveStateHandler: Saved, sending response. (%s)", abstractPlayer.getUuid().toString()));
                });
            }
        }
    }

}
