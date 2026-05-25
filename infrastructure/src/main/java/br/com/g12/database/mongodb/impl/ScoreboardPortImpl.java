package br.com.g12.database.mongodb.impl;

import br.com.g12.database.mongodb.ScoreboardRepository;
import br.com.g12.entity.ScoreboardDocument;
import br.com.g12.model.CompetitionDefaults;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.ScoreboardPort;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScoreboardPortImpl implements ScoreboardPort {

    private final ScoreboardRepository repository;
    private final MongoTemplate mongoTemplate;

    public ScoreboardPortImpl(ScoreboardRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Scoreboard> findByCompetitionIdAndRoundAndYear(String competitionId, int round, int year) {
        Query query = new Query(new Criteria().andOperator(
                competitionCriteria(competitionId),
                Criteria.where("round").is(round),
                Criteria.where("year").is(year)
        )).with(Sort.by(Sort.Direction.DESC, "points"));

        return mongoTemplate.find(query, ScoreboardDocument.class).stream()
                .map(ScoreboardDocument::toModel)
                .toList();
    }

    @Override
    public void saveAll(List<Scoreboard> scoreboards) {

        List<ScoreboardDocument> documents = scoreboards.stream()
                .map(ScoreboardDocument::fromModel)
                .toList();

       repository.saveAll(documents);
    }

    @Override
    public Scoreboard findByCompetitionIdAndRoundAndYearAndUsername(String competitionId, int round, int year, String username) {
        Query query = new Query(new Criteria().andOperator(
                competitionCriteria(competitionId),
                Criteria.where("round").is(round),
                Criteria.where("year").is(year),
                Criteria.where("username").is(username)
        ));

        ScoreboardDocument document = mongoTemplate.findOne(query, ScoreboardDocument.class);
        return document != null ? document.toModel() : null;
    }

    @Override
    public List<Scoreboard> findByCompetitionIdAndRoundAndYearAndUsernames(String competitionId, int round, int year, List<String> usernames) {
        Query query = new Query(new Criteria().andOperator(
                competitionCriteria(competitionId),
                Criteria.where("round").is(round),
                Criteria.where("year").is(year),
                Criteria.where("username").in(usernames)
        ));

        List<ScoreboardDocument> documents = mongoTemplate.find(query, ScoreboardDocument.class);
        return documents.stream()
                .map(ScoreboardDocument::toModel)
                .toList();
    }

    @Override
    public void save(Scoreboard scoreboard) {
        repository.save(ScoreboardDocument.fromModel(scoreboard));
    }

    private Criteria competitionCriteria(String competitionId) {
        String normalizedCompetitionId = CompetitionDefaults.competitionIdOrDefault(competitionId);
        Criteria currentCompetition = Criteria.where("competitionId").is(normalizedCompetitionId);

        if (!CompetitionDefaults.DEFAULT_COMPETITION_ID.equals(normalizedCompetitionId)) {
            return currentCompetition;
        }

        return new Criteria().orOperator(
                currentCompetition,
                Criteria.where("competitionId").exists(false),
                Criteria.where("competitionId").is(null)
        );
    }
}
