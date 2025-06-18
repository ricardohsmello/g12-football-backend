package br.com.g12.database.mongodb;

import br.com.g12.entity.MatchDocument;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class SimpleMongoTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Test
    void should_insert_and_read_document() {
        try (var mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoTemplate template = new MongoTemplate(mongoClient, "testdb");

            Person person = new Person("Ricardo", 30);
            template.save(person);

            long count = template.count(Query.query(Criteria.where("name").is("Ricardo")), Person.class);
            assertEquals(1, count);
        }
    }

    @Test
    void should_save_match_date_without_millis() {
        try (var mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoTemplate template = new MongoTemplate(mongoClient, "testdb");

            ZonedDateTime zdt = ZonedDateTime.of(2025, 6, 17, 18, 0, 0, 0, ZoneId.of("America/Sao_Paulo"));
            Date date = Date.from(zdt.toInstant());

            System.out.println("Local: " + zdt); // 2025-06-17T18:00-03:00[America/Sao_Paulo]
            System.out.println("UTC Date: " + Date.from(zdt.toInstant()));


//            Date inputDate = getDateWithoutMillis(15, 0, 0); // 15:00:00.000

            MatchDocument matchDocument = new MatchDocument(
                    "1", 1, "Time A", "Time B", date, null, "OPEN"
            );

            template.save(matchDocument);

            MatchDocument saved = template.findOne(
                    Query.query(Criteria.where("_id").is("1")), MatchDocument.class
            );

            assertEquals(date, saved.getMatchDate());
            assertEquals(0, saved.getMatchDate().getTime() % 1000); // millisecond zero
        }
    }

    private Date getDateWithoutMillis(int hour, int minute, int second) {
        var calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}
