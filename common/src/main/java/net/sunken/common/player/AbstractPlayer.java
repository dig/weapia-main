package net.sunken.common.player;

import lombok.Data;
import lombok.ToString;
import net.sunken.common.database.MongoSerializable;
import org.bson.Document;

import java.util.UUID;

@Data
@ToString
public abstract class AbstractPlayer implements MongoSerializable {

    protected final UUID uuid;
    protected final String username;
    protected Rank rank;

    public AbstractPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.rank = Rank.PLAYER;
    }

    public boolean fromDocument(Document document) {
        rank = Rank.valueOf(document.getString("rank"));
        return true;
    }

    public Document toDocument() {
        Document document = new Document()
                .append("uuid", uuid.toString())
                .append("username", username)
                .append("rank", rank.toString());
        return document;
    }

    /**
     * Called on successful join.
     */
    public abstract void setup();

    /**
     * Called on successful leave.
     */
    public abstract void destroy();

    public PlayerDetail toPlayerDetail() {
        return new PlayerDetail(uuid, username, rank);
    }

}
