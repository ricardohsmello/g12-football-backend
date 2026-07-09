package br.com.g12.config.encryption;

import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.g12.config.encryption.MongoEncryptionSupport.BET_COLLECTION;

@Component
@ConditionalOnProperty(prefix = "g12.encryption.migration", name = "enabled", havingValue = "true")
public class BetPredictionEncryptionMigration implements ApplicationRunner, Ordered {

    static final String BACKUP_COLLECTION = "bet_plain_backup";
    private static final int BATCH_SIZE = 200;

    private static final Logger log = LoggerFactory.getLogger(BetPredictionEncryptionMigration.class);

    private final MongoClient encryptedClient;
    private final MongoProperties mongoProperties;
    private final BetEncryptedFields betEncryptedFields;

    public BetPredictionEncryptionMigration(MongoClient encryptedClient,
                                            MongoProperties mongoProperties,
                                            BetEncryptedFields betEncryptedFields) {
        this.encryptedClient = encryptedClient;
        this.mongoProperties = mongoProperties;
        this.betEncryptedFields = betEncryptedFields;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void run(ApplicationArguments args) {
        String database = mongoProperties.getDatabase();

        try (MongoClient plainClient = MongoClients.create(mongoProperties.getUri())) {
            MongoDatabase plainDb = plainClient.getDatabase(database);

            Document betInfo = MongoEncryptionSupport.collectionInfo(plainDb, BET_COLLECTION);
            if (betInfo == null) {
                log.info("Encryption migration: no '{}' collection found, nothing to migrate", BET_COLLECTION);
                return;
            }
            if (MongoEncryptionSupport.hasEncryptedFields(betInfo)) {
                log.info("Encryption migration: '{}' already uses Queryable Encryption, nothing to do", BET_COLLECTION);
                return;
            }
            if (MongoEncryptionSupport.collectionInfo(plainDb, BACKUP_COLLECTION) != null) {
                throw new IllegalStateException("Encryption migration aborted: backup collection '" + BACKUP_COLLECTION
                        + "' already exists. Verify/drop it before retrying.");
            }

            long total = plainDb.getCollection(BET_COLLECTION).countDocuments();
            log.info("Encryption migration: migrating {} bets to Queryable Encryption", total);

            plainDb.getCollection(BET_COLLECTION)
                    .renameCollection(new MongoNamespace(database, BACKUP_COLLECTION));

            encryptedClient.getDatabase(database).createCollection(BET_COLLECTION,
                    new CreateCollectionOptions().encryptedFields(betEncryptedFields.encryptedFields()));

            copyIndexes(plainDb);
            copyDocuments(plainDb.getCollection(BACKUP_COLLECTION),
                    encryptedClient.getDatabase(database).getCollection(BET_COLLECTION));

            long migrated = plainDb.getCollection(BET_COLLECTION).countDocuments();
            if (migrated != total) {
                throw new IllegalStateException("Encryption migration incomplete: expected " + total
                        + " bets but the encrypted collection has " + migrated
                        + ". Plaintext data is intact in '" + BACKUP_COLLECTION + "'.");
            }
            log.info("Encryption migration finished: {} bets encrypted. Verify the data, then drop '{}' manually "
                    + "and set g12.encryption.migration.enabled back to false.", migrated, BACKUP_COLLECTION);
        }
    }

    private void copyIndexes(MongoDatabase plainDb) {
        for (Document index : plainDb.getCollection(BACKUP_COLLECTION).listIndexes()) {
            String name = index.getString("name");
            if ("_id_".equals(name)) {
                continue;
            }
            plainDb.getCollection(BET_COLLECTION)
                    .createIndex(index.get("key", Document.class), new IndexOptions().name(name));
        }
    }

    private void copyDocuments(MongoCollection<Document> source, MongoCollection<Document> target) {
        List<Document> batch = new ArrayList<>(BATCH_SIZE);
        for (Document document : source.find()) {
            batch.add(document);
            if (batch.size() == BATCH_SIZE) {
                target.insertMany(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            target.insertMany(batch);
        }
    }
}
