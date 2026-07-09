package br.com.g12.config.encryption;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static br.com.g12.config.encryption.MongoEncryptionSupport.AWAY_TEAM_KEY_ALT_NAME;
import static br.com.g12.config.encryption.MongoEncryptionSupport.BET_COLLECTION;
import static br.com.g12.config.encryption.MongoEncryptionSupport.HOME_TEAM_KEY_ALT_NAME;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MongoEncryptionProperties.class)
@ConditionalOnProperty(prefix = "g12.encryption", name = "enabled", havingValue = "true")
public class MongoEncryptionConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoEncryptionConfig.class);

    @Bean
    public BetEncryptedFields betEncryptedFields(MongoEncryptionProperties properties, MongoProperties mongoProperties) {
        MongoEncryptionSupport.preloadCryptShared(properties.cryptSharedLibPath());

        Map<String, Map<String, Object>> kmsProviders = MongoEncryptionSupport.localKmsProviders(properties.masterKey());
        String uri = mongoProperties.getUri();

        try (MongoClient bootstrapClient = MongoClients.create(uri);
             ClientEncryption clientEncryption = ClientEncryptions.create(
                     MongoEncryptionSupport.clientEncryptionSettings(uri, properties.keyVaultNamespace(), kmsProviders))) {

            MongoEncryptionSupport.ensureKeyVaultIndex(bootstrapClient, properties.keyVaultNamespace());

            BsonBinary homeTeamKeyId = MongoEncryptionSupport.ensureDataKey(clientEncryption, HOME_TEAM_KEY_ALT_NAME);
            BsonBinary awayTeamKeyId = MongoEncryptionSupport.ensureDataKey(clientEncryption, AWAY_TEAM_KEY_ALT_NAME);

            BsonDocument encryptedFields = MongoEncryptionSupport.betEncryptedFields(homeTeamKeyId, awayTeamKeyId);

            ensureEncryptedBetCollection(bootstrapClient, mongoProperties.getDatabase(), encryptedFields);

            return new BetEncryptedFields(mongoProperties.getDatabase(), encryptedFields);
        }
    }

    @Bean
    public MongoClientSettingsBuilderCustomizer autoEncryptionCustomizer(
            BetEncryptedFields betEncryptedFields, MongoEncryptionProperties properties) {

        Map<String, Object> extraOptions = new HashMap<>();
        if (properties.cryptSharedLibPath() != null && !properties.cryptSharedLibPath().isBlank()) {
            extraOptions.put("cryptSharedLibPath", properties.cryptSharedLibPath());
            extraOptions.put("cryptSharedLibRequired", true);
        }

        AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
                .keyVaultNamespace(properties.keyVaultNamespace())
                .kmsProviders(MongoEncryptionSupport.localKmsProviders(properties.masterKey()))
                .encryptedFieldsMap(Map.of(betEncryptedFields.namespace(), betEncryptedFields.encryptedFields()))
                .extraOptions(extraOptions)
                .build();

        return builder -> builder.autoEncryptionSettings(autoEncryptionSettings);
    }

    private void ensureEncryptedBetCollection(MongoClient client, String database, BsonDocument encryptedFields) {
        MongoDatabase db = client.getDatabase(database);
        Document existing = MongoEncryptionSupport.collectionInfo(db, BET_COLLECTION);

        if (existing == null) {
            db.createCollection(BET_COLLECTION, new CreateCollectionOptions().encryptedFields(encryptedFields));
            log.info("Created '{}' collection with Queryable Encryption for prediction.homeTeam/awayTeam", BET_COLLECTION);
        } else if (!MongoEncryptionSupport.hasEncryptedFields(existing)) {
            log.warn("Collection '{}' exists without Queryable Encryption; writes to it will fail. "
                    + "Set g12.encryption.migration.enabled=true to migrate the existing bets.", BET_COLLECTION);
        }
    }
}
