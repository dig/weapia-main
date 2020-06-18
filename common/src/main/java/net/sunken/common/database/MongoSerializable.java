package net.sunken.common.database;

import org.bson.Document;

public interface MongoSerializable {

    boolean fromDocument(Document document);
    Document toDocument();

}
