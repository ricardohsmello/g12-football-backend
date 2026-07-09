package br.com.g12.database.mongodb;

import br.com.g12.config.encryption.MongoEncryptionSupport;
import br.com.g12.database.mongodb.impl.BetPortImpl;
import br.com.g12.entity.BetDocument;
import br.com.g12.model.Bet;
import br.com.g12.model.Score;
import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static br.com.g12.config.encryption.MongoEncryptionSupport.AWAY_TEAM_KEY_ALT_NAME;
import static br.com.g12.config.encryption.MongoEncryptionSupport.BET_COLLECTION;
import static br.com.g12.config.encryption.MongoEncryptionSupport.HOME_TEAM_KEY_ALT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Testcontainers
public class BetPredictionEncryptionTest {

    private static final String KEY_VAULT_NAMESPACE = "encryption.__keyVault";
    private static final int ENCRYPTED_BINARY_SUBTYPE = 6;

    @Container
    static MongoDBContainer mongo = new MongoDBContainer(
            DockerImageName.parse("mongodb/mongodb-enterprise-server:8.0-ubi8")
                    .asCompatibleSubstituteFor("mongo"));

    static Map<String, Map<String, Object>> kmsProviders;

    @BeforeAll
    static void createMasterKey() {
        byte[] masterKey = new byte[96];
        new SecureRandom().nextBytes(masterKey);
        kmsProviders = MongoEncryptionSupport.localKmsProviders(Base64.getEncoder().encodeToString(masterKey));
    }

    @Test
    void prediction_is_ciphertext_in_mongodb_but_readable_through_the_driver() {
        String database = "g12_explicit";
        String uri = mongo.getReplicaSetUrl(database);

        try (MongoClient plainClient = MongoClients.create(uri);
             ClientEncryption clientEncryption = ClientEncryptions.create(
                     MongoEncryptionSupport.clientEncryptionSettings(uri, KEY_VAULT_NAMESPACE, kmsProviders))) {

            BsonBinary homeKeyId = MongoEncryptionSupport.ensureDataKey(clientEncryption, HOME_TEAM_KEY_ALT_NAME);
            BsonBinary awayKeyId = MongoEncryptionSupport.ensureDataKey(clientEncryption, AWAY_TEAM_KEY_ALT_NAME);
            BsonDocument encryptedFields = MongoEncryptionSupport.betEncryptedFields(homeKeyId, awayKeyId);

            MongoDatabase plainDb = plainClient.getDatabase(database);
            plainDb.createCollection(BET_COLLECTION, new CreateCollectionOptions().encryptedFields(encryptedFields));

            // decrypting client: no query analysis needed, decryption is always automatic
            AutoEncryptionSettings decryptionSettings = AutoEncryptionSettings.builder()
                    .keyVaultNamespace(KEY_VAULT_NAMESPACE)
                    .kmsProviders(kmsProviders)
                    .bypassQueryAnalysis(true)
                    .build();

            try (MongoClient decryptingClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .autoEncryptionSettings(decryptionSettings)
                    .build())) {

                BsonBinary encryptedHome = clientEncryption.encrypt(new BsonInt32(2),
                        new EncryptOptions("Unindexed").keyId(homeKeyId));
                BsonBinary encryptedAway = clientEncryption.encrypt(new BsonInt32(1),
                        new EncryptOptions("Unindexed").keyId(awayKeyId));

                decryptingClient.getDatabase(database).getCollection(BET_COLLECTION)
                        .insertOne(new Document("username", "ricardomello")
                                .append("prediction", new Document("homeTeam", encryptedHome)
                                        .append("awayTeam", encryptedAway)));

                // the DBA view: raw client without keys sees only BinData subtype 6
                Document raw = plainDb.getCollection(BET_COLLECTION).find().first();
                assertNotNull(raw);
                Document rawPrediction = raw.get("prediction", Document.class);
                assertInstanceOf(Binary.class, rawPrediction.get("homeTeam"));
                assertInstanceOf(Binary.class, rawPrediction.get("awayTeam"));
                assertEquals(ENCRYPTED_BINARY_SUBTYPE, ((Binary) rawPrediction.get("homeTeam")).getType());
                assertEquals(ENCRYPTED_BINARY_SUBTYPE, ((Binary) rawPrediction.get("awayTeam")).getType());

                // the application view: values decrypted transparently
                Document decrypted = decryptingClient.getDatabase(database)
                        .getCollection(BET_COLLECTION).find().first();
                assertNotNull(decrypted);
                Document prediction = decrypted.get("prediction", Document.class);
                assertEquals(2, prediction.getInteger("homeTeam"));
                assertEquals(1, prediction.getInteger("awayTeam"));
            }
        }
    }

    @Test
    void save_all_scores_bets_one_by_one_on_the_encrypted_collection() {
        String cryptSharedLibPath = System.getenv("CRYPT_SHARED_LIB_PATH");
        Assumptions.assumeTrue(cryptSharedLibPath != null && !cryptSharedLibPath.isBlank(),
                "CRYPT_SHARED_LIB_PATH is not set - skipping automatic-encryption test");
        MongoEncryptionSupport.preloadCryptShared(cryptSharedLibPath);

        String database = "g12_saveall";
        String uri = mongo.getReplicaSetUrl(database);

        try (MongoClient plainClient = MongoClients.create(uri);
             ClientEncryption clientEncryption = ClientEncryptions.create(
                     MongoEncryptionSupport.clientEncryptionSettings(uri, KEY_VAULT_NAMESPACE, kmsProviders))) {

            BsonBinary homeKeyId = MongoEncryptionSupport.ensureDataKey(clientEncryption, HOME_TEAM_KEY_ALT_NAME);
            BsonBinary awayKeyId = MongoEncryptionSupport.ensureDataKey(clientEncryption, AWAY_TEAM_KEY_ALT_NAME);
            BsonDocument encryptedFields = MongoEncryptionSupport.betEncryptedFields(homeKeyId, awayKeyId);

            MongoDatabase plainDb = plainClient.getDatabase(database);
            plainDb.createCollection(BET_COLLECTION, new CreateCollectionOptions().encryptedFields(encryptedFields));

            AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
                    .keyVaultNamespace(KEY_VAULT_NAMESPACE)
                    .kmsProviders(kmsProviders)
                    .encryptedFieldsMap(Map.of(database + "." + BET_COLLECTION, encryptedFields))
                    .extraOptions(Map.of(
                            "cryptSharedLibPath", cryptSharedLibPath,
                            "cryptSharedLibRequired", true))
                    .build();

            try (MongoClient encryptedClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .autoEncryptionSettings(autoEncryptionSettings)
                    .build())) {

                MongoTemplate template = new MongoTemplate(encryptedClient, database);
                BetPortImpl betPort = new BetPortImpl(null, template);

                String firstBetId = new ObjectId().toHexString();
                String secondBetId = new ObjectId().toHexString();
                String matchId = new ObjectId().toHexString();

                // scoring a round rewrites several bets at once with their earned points
                betPort.saveAll(java.util.List.of(
                        new Bet(firstBetId, matchId, "ricardomello", new Score(2, 1), 13, 5, new Date()),
                        new Bet(secondBetId, matchId, "outrouser", new Score(0, 0), 13, 0, new Date())));

                Document raw = plainDb.getCollection(BET_COLLECTION).find().first();
                assertNotNull(raw);
                Document rawPrediction = raw.get("prediction", Document.class);
                assertInstanceOf(Binary.class, rawPrediction.get("homeTeam"));

                BetDocument loaded = template.findById(firstBetId, BetDocument.class);
                assertNotNull(loaded);
                assertEquals(new Score(2, 1), loaded.toModel().getPrediction());
                assertEquals(5, loaded.toModel().getPointsEarned());

                // distinct-bettor count must not use the distinct command (unsupported by QE)
                assertEquals(2, betPort.countDistinctUsernamesByCompetitionIdAndRound(null, 13));
            }
        }
    }

    @Test
    void mongo_template_encrypts_and_decrypts_bets_automatically() {
        String cryptSharedLibPath = System.getenv("CRYPT_SHARED_LIB_PATH");
        Assumptions.assumeTrue(cryptSharedLibPath != null && !cryptSharedLibPath.isBlank(),
                "CRYPT_SHARED_LIB_PATH is not set - skipping automatic-encryption test");
        MongoEncryptionSupport.preloadCryptShared(cryptSharedLibPath);

        String database = "g12_auto";
        String uri = mongo.getReplicaSetUrl(database);

        try (MongoClient plainClient = MongoClients.create(uri);
             ClientEncryption clientEncryption = ClientEncryptions.create(
                     MongoEncryptionSupport.clientEncryptionSettings(uri, KEY_VAULT_NAMESPACE, kmsProviders))) {

            BsonBinary homeKeyId = MongoEncryptionSupport.ensureDataKey(clientEncryption, HOME_TEAM_KEY_ALT_NAME);
            BsonBinary awayKeyId = MongoEncryptionSupport.ensureDataKey(clientEncryption, AWAY_TEAM_KEY_ALT_NAME);
            BsonDocument encryptedFields = MongoEncryptionSupport.betEncryptedFields(homeKeyId, awayKeyId);

            MongoDatabase plainDb = plainClient.getDatabase(database);
            plainDb.createCollection(BET_COLLECTION, new CreateCollectionOptions().encryptedFields(encryptedFields));

            AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
                    .keyVaultNamespace(KEY_VAULT_NAMESPACE)
                    .kmsProviders(kmsProviders)
                    .encryptedFieldsMap(Map.of(database + "." + BET_COLLECTION, encryptedFields))
                    .extraOptions(Map.of(
                            "cryptSharedLibPath", cryptSharedLibPath,
                            "cryptSharedLibRequired", true))
                    .build();

            try (MongoClient encryptedClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .autoEncryptionSettings(autoEncryptionSettings)
                    .build())) {

                MongoTemplate template = new MongoTemplate(encryptedClient, database);

                template.save(new BetDocument(null, new ObjectId().toHexString(), "ricardomello",
                        new Score(2, 1), 13, null, new Date()));

                Document raw = plainDb.getCollection(BET_COLLECTION).find().first();
                assertNotNull(raw);
                Document rawPrediction = raw.get("prediction", Document.class);
                assertInstanceOf(Binary.class, rawPrediction.get("homeTeam"));
                assertInstanceOf(Binary.class, rawPrediction.get("awayTeam"));
                assertEquals(ENCRYPTED_BINARY_SUBTYPE, ((Binary) rawPrediction.get("homeTeam")).getType());

                BetDocument loaded = template.findOne(
                        query(where("username").is("ricardomello")), BetDocument.class);
                assertNotNull(loaded);
                assertEquals(new Score(2, 1), loaded.toModel().getPrediction());
            }
        }
    }
}
