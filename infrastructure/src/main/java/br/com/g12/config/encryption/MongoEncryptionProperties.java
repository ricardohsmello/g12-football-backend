package br.com.g12.config.encryption;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;


@ConfigurationProperties(prefix = "g12.encryption")
public record MongoEncryptionProperties(
        @DefaultValue("false") boolean enabled,
        String masterKey,
        @DefaultValue("encryption.__keyVault") String keyVaultNamespace,
        String cryptSharedLibPath,
        @DefaultValue Migration migration) {

    public record Migration(@DefaultValue("false") boolean enabled) {}
}
