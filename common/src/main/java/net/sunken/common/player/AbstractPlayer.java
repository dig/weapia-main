package net.sunken.common.player;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.sunken.common.database.DatabaseHelper;
import net.sunken.common.database.MongoSerializable;
import net.sunken.common.util.MongoUtil;
import org.bson.Document;

import java.util.UUID;

@Data
@ToString
public abstract class AbstractPlayer implements MongoSerializable {

    @Getter @Setter
    protected boolean saved;

    protected final UUID uuid;
    protected final String username;

    protected Rank rank;
    protected long firstLoginMillis;
    protected long lastLoginMillis;

    public AbstractPlayer(UUID uuid, String username) {
        this.saved = false;

        this.uuid = uuid;
        this.username = username;

        this.rank = Rank.PLAYER;
        this.firstLoginMillis = System.currentTimeMillis();
        this.lastLoginMillis = System.currentTimeMillis();
    }

    public boolean fromDocument(Document document) {
        rank = (Rank) MongoUtil.getEnumOrDefault(document, Rank.class, DatabaseHelper.PLAYER_RANK_KEY, Rank.PLAYER);
        firstLoginMillis = document.getLong(DatabaseHelper.PLAYER_FIRSTLOGIN_KEY);
        return true;
    }

    public Document toDocument() {
        Document document = new Document()
                .append(DatabaseHelper.PLAYER_UUID_KEY, uuid.toString())
                .append(DatabaseHelper.PLAYER_USERNAME_KEY, username)
                .append(DatabaseHelper.PLAYER_RANK_KEY, rank.toString())
                .append(DatabaseHelper.PLAYER_FIRSTLOGIN_KEY, firstLoginMillis)
                .append(DatabaseHelper.PLAYER_LASTLOGIN_KEY, lastLoginMillis);
        return document;
    }

    public PlayerDetail toPlayerDetail() {
        return new PlayerDetail(uuid, username, rank);
    }
}
