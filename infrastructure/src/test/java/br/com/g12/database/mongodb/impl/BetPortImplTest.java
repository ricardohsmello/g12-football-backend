package br.com.g12.database.mongodb.impl;

import br.com.g12.entity.BetDocument;
import br.com.g12.model.Score;
import com.mongodb.client.MongoClients;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class BetPortImplTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Test
    void should_count_distinct_bettors_by_round_for_current_year() {
        try (var mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoTemplate template = new MongoTemplate(mongoClient, "testdb");
            BetPortImpl betPort = new BetPortImpl(null, template);
            int currentYear = LocalDate.now().getYear();

            template.save(bet("lucas", 13, currentYear));
            template.save(bet("lucas", 13, currentYear));
            template.save(bet("ana", 13, currentYear));
            template.save(bet("bia", 13, currentYear - 1));
            template.save(bet("caio", 14, currentYear));

            int count = betPort.countDistinctUsernamesByRound(13);

            assertEquals(2, count);
        }
    }

    private BetDocument bet(String username, int round, int year) {
        return new BetDocument(
                null,
                new ObjectId().toHexString(),
                username,
                new Score(1, 0),
                round,
                null,
                dateInYear(year)
        );
    }

    private Date dateInYear(int year) {
        return Date.from(LocalDate.of(year, 6, 1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }
}
