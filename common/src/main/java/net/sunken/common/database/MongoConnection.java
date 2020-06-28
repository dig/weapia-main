package net.sunken.common.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.database.config.MongoConfiguration;
import org.bson.Document;

@Singleton
public class MongoConnection extends Database<MongoClient> {

    private MongoClient mongoClient;

    @Inject
    public MongoConnection(@InjectConfig MongoConfiguration mongoConfiguration) {
        MongoCredential credential = MongoCredential.createCredential(mongoConfiguration.getUsername(), mongoConfiguration.getUserDatabase(), mongoConfiguration.getPassword().toCharArray());
        MongoClientOptions options = MongoClientOptions.builder().build();
        mongoClient = new MongoClient(new ServerAddress(mongoConfiguration.getHost(), mongoConfiguration.getPort()), credential, options);
    }

    public MongoDatabase getDatabase(@NonNull String database) {
        return mongoClient.getDatabase(database);
    }

    public MongoCollection<Document> getCollection(@NonNull String database, @NonNull String collection) {
        return getDatabase(database).getCollection(collection);
    }

    @Override
    public MongoClient getConnection() {
        return mongoClient;
    }

    @Override
    public void disconnect() {
        mongoClient.close();
    }
}
