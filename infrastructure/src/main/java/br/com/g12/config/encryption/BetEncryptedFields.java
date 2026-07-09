package br.com.g12.config.encryption;

import org.bson.BsonDocument;

public record BetEncryptedFields(String database, BsonDocument encryptedFields) {

    public String namespace() {
        return database + "." + MongoEncryptionSupport.BET_COLLECTION;
    }
}
