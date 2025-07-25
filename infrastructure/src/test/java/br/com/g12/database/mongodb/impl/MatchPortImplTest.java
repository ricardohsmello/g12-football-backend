package br.com.g12.database.mongodb.impl;

import br.com.g12.database.mongodb.BaseTestContainer;
import br.com.g12.database.mongodb.MatchRepository;
import br.com.g12.entity.MatchDocument;
import br.com.g12.model.Match;
import br.com.g12.port.MatchPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;
import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class MatchPortImplTest  extends BaseTestContainer<MatchDocument> {

    private MatchPort matchPort;

    @Override
    public Class<MatchDocument> clazzType() {
        return MatchDocument.class;
    }

    @Override
    public String collectionName() {
        return "match-test";
    }

    @BeforeEach
    void setup() {
        deleteAll();
        MongoRepositoryFactory factory = new MongoRepositoryFactory(getMongoTemplate());
        MatchRepository matchRepository = factory.getRepository(MatchRepository.class);
        this.matchPort = new MatchPortImpl(matchRepository, getMongoTemplate());
    }

    @Test
    void should_save_and_return_match() {
        Match match = new Match(null, 13, "Corinthians", "São Paulo", new Date(), null, "OPEN");
        Match saved = matchPort.save(match);

        assertNotNull(saved.getId());
        assertEquals("Corinthians", saved.getHomeTeam());
    }

    @Test
    void should_find_by_match_id_and_return_match() {
        Match saved = matchPort.save(new Match("123", 13, "Corinthians", "São Paulo", new Date(), null, "OPEN"));
        assertNotNull(saved.getId());

        Match find = matchPort.find(saved.getId());
        assertEquals(saved.getId(), find.getId());
    }

    @Test
    void should_find_two_matches_by_round_and_status() {
        matchPort.save(new Match("1", 23, "Corinthians", "Vasco", new Date(), null, "OPEN"));
        matchPort.save(new Match("2", 23, "Flamengo", "São Paulo", new Date(), null, "OPEN"));
        matchPort.save(new Match("3", 5, "Mirassol", "Cruzeiro", new Date(), null, "OPEN"));

        List<Match> matches = matchPort.findByRoundAndStatus(23, "OPEN");

        assertEquals(2, matches.size());
        assertEquals("Corinthians", matches.getFirst().getHomeTeam());
        assertEquals("Flamengo", matches.getLast().getHomeTeam());
    }


//    @Override
//    public List<Match> findByRoundAndStatus(int round, String status) {
//        List<MatchDocument> byRound = matchRepository.findByRoundAndStatus(round, status);
//        return byRound.stream().map(MatchDocument::toModel).toList();
//    }
}
