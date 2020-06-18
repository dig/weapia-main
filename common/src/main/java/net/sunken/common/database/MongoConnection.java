package net.sunken.common.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.database.config.MongoConfiguration;

import java.util.Arrays;

@Singleton
public class MongoConnection extends Database<MongoClient> {

    private MongoClient mongoClient;

    @Inject
    public MongoConnection(@InjectConfig MongoConfiguration mongoConfiguration) {
        MongoCredential credential = MongoCredential.createCredential(mongoConfiguration.getUsername(), mongoConfiguration.getUserDatabase(), mongoConfiguration.getPassword().toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .build();

        mongoClient = new MongoClient(new ServerAddress(mongoConfiguration.getHost(), mongoConfiguration.getPort()), credential, options);
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
