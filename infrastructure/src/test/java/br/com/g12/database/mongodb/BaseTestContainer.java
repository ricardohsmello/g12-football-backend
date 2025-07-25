package br.com.g12.database.mongodb;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.mongodb.client.MongoClients;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

public abstract class BaseTestContainer<T> {

    private final MongoTemplate mongoTemplate;
    public abstract Class<T> clazzType();

    public abstract String collectionName();

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0.0")
            .withExposedPorts(27017)
            .withCreateContainerCmdModifier(cmd -> cmd.withPortBindings(
                    new PortBinding(Ports.Binding.bindPort(27018), new ExposedPort(27017))
            ));

    public BaseTestContainer() {
        this.mongoTemplate = new MongoTemplate(
                MongoClients.create(mongoDBContainer.getConnectionString()),
                "g12-test"
        );
    }

    public T insertOne(T document) {
        return mongoTemplate.insert(document, collectionName());
    }

    public List<T> findAll() {
        return mongoTemplate.findAll(clazzType(), collectionName());
    }

    public void deleteAll() {
        mongoTemplate.remove(
                new org.springframework.data.mongodb.core.query.Query(),
                collectionName()
        );
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
