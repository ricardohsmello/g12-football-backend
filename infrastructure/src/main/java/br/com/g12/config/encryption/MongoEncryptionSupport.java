package br.com.g12.config.encryption;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.ClientEncryptionSettings;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public final class MongoEncryptionSupport {

    public static final String BET_COLLECTION = "bet";
    public static final String HOME_TEAM_KEY_ALT_NAME = "g12-bet-prediction-homeTeam";
    public static final String AWAY_TEAM_KEY_ALT_NAME = "g12-bet-prediction-awayTeam";

    private MongoEncryptionSupport() {}

    public static Map<String, Map<String, Object>> localKmsProviders(String base64MasterKey) {
        if (base64MasterKey == null || base64MasterKey.isBlank()) {
            throw new IllegalStateException(
                    "g12.encryption.master-key (env MONGODB_MASTER_KEY) is required when encryption is enabled. "
                            + "Generate one with: openssl rand -base64 96");
        }

        byte[] masterKey = Base64.getDecoder().decode(base64MasterKey.replaceAll("\\s", ""));
        if (masterKey.length != 96) {
            throw new IllegalStateException(
                    "The local KMS master key must be exactly 96 bytes, but the configured key has "
                            + masterKey.length + " bytes");
        }
        return Map.of("local", Map.of("key", masterKey));
    }

    public static ClientEncryptionSettings clientEncryptionSettings(
            String uri, String keyVaultNamespace, Map<String, Map<String, Object>> kmsProviders) {
        return ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(uri))
                        .build())
                .keyVaultNamespace(keyVaultNamespace)
                .kmsProviders(kmsProviders)
                .build();
    }

    public static BsonBinary ensureDataKey(ClientEncryption clientEncryption, String keyAltName) {
        BsonDocument existing = clientEncryption.getKeyByAltName(keyAltName);
        if (existing != null) {
            return existing.getBinary("_id");
        }
        return clientEncryption.createDataKey("local", new DataKeyOptions().keyAltNames(List.of(keyAltName)));
    }

    public static BsonDocument betEncryptedFields(BsonBinary homeTeamKeyId, BsonBinary awayTeamKeyId) {
        return new BsonDocument("fields", new BsonArray(List.of(
                encryptedIntField("prediction.homeTeam", homeTeamKeyId),
                encryptedIntField("prediction.awayTeam", awayTeamKeyId))));
    }

    private static BsonDocument encryptedIntField(String path, BsonBinary keyId) {
        return new BsonDocument()
                .append("path", new BsonString(path))
                .append("bsonType", new BsonString("int"))
                .append("keyId", keyId);
    }

    public static void ensureKeyVaultIndex(MongoClient client, String keyVaultNamespace) {
        MongoNamespace namespace = new MongoNamespace(keyVaultNamespace);
        client.getDatabase(namespace.getDatabaseName())
                .getCollection(namespace.getCollectionName())
                .createIndex(Indexes.ascending("keyAltNames"),
                        new IndexOptions().unique(true)
                                .partialFilterExpression(Filters.exists("keyAltNames")));
    }

    public static void preloadCryptShared(String cryptSharedLibPath) {
        if (cryptSharedLibPath == null || cryptSharedLibPath.isBlank()) {
            return;
        }
        try {
            System.load(cryptSharedLibPath);
        } catch (UnsatisfiedLinkError e) {
            throw new IllegalStateException(
                    "Failed to load the crypt_shared library at '" + cryptSharedLibPath
                            + "'. Check that the file is the correct build for this OS/architecture "
                            + "(Windows: mongo_crypt_v1.dll from the windows-x86_64 package): " + e.getMessage(), e);
        }
    }

    public static Document collectionInfo(MongoDatabase database, String collectionName) {
        return database.listCollections()
                .filter(Filters.eq("name", collectionName))
                .first();
    }

    public static boolean hasEncryptedFields(Document collectionInfo) {
        Document options = collectionInfo.get("options", Document.class);
        return options != null && options.get("encryptedFields") != null;
    }
}
