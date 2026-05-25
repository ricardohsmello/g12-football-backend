package br.com.g12.database.mongodb.impl;


import br.com.g12.database.mongodb.MatchRepository;
import br.com.g12.entity.MatchDocument;
import br.com.g12.model.CompetitionDefaults;
import br.com.g12.model.Match;
import br.com.g12.model.MatchWithPrediction;
import br.com.g12.port.MatchPort;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class MatchPortImpl implements MatchPort {

    private final MatchRepository matchRepository;

    private final MongoTemplate mongoTemplate;

    MatchPortImpl(MatchRepository matchRepository, MongoTemplate mongoTemplate) {
        this.matchRepository = matchRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Match save(Match match) {
        MatchDocument matchDocument = MatchDocument.fromModel(match);
        return matchRepository.save(matchDocument).toModel();
    }

    @Override
    public Match find(String id) {
        Optional<MatchDocument> byId = matchRepository.findById(id);
        return byId.map(MatchDocument::toModel).orElse(null);
    }

    @Override
    public List<Match> findByCompetitionIdAndRoundAndStatus(String competitionId, int round, String status) {
        Query query = new Query(new Criteria().andOperator(
                competitionCriteria(competitionId),
                Criteria.where("round").is(round),
                Criteria.where("status").is(status)
        ));

        List<MatchDocument> byRound = mongoTemplate.find(query, MatchDocument.class);
        return byRound.stream().map(MatchDocument::toModel).toList();
    }

    @Override
    public List<Match> findByCompetitionIdAndRoundAndStatusAndMatchDateBetween(String competitionId, int round, String status, Date startDate, Date endDate) {
        Query query = new Query(new Criteria().andOperator(
                competitionCriteria(competitionId),
                Criteria.where("round").is(round),
                Criteria.where("status").is(status),
                Criteria.where("matchDate").gte(startDate).lt(endDate)
        ));

        List<MatchDocument> byRound = mongoTemplate.find(query, MatchDocument.class);
        return byRound.stream().map(MatchDocument::toModel).toList();
    }

    @Override
    public List<MatchWithPrediction> findByCompetitionIdAndRoundUserAndYear(String competitionId, String username, int round, int year) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(new Criteria().andOperator(
                        competitionCriteria(competitionId),
                        Criteria.where("round").is(round),
                        Criteria.where("matchDate").gte(startOfYear(year)).lt(startOfYear(year + 1))
                )),
                lookup()
                        .from("bet")
                        .localField("_id")
                        .foreignField("matchId")
                        .pipeline(
                                match(Criteria.where("username").is(username))
                        )
                        .as("userPrediction"),

                addFields()
                        .addField("prediction")
                        .withValue(ArrayOperators.ArrayElemAt.arrayOf("userPrediction.prediction").elementAt(0))
                        .addField("pointsEarned")
                        .withValue(ArrayOperators.ArrayElemAt.arrayOf("userPrediction.pointsEarned").elementAt(0))
                        .build(),

                Aggregation.project(
                        "competitionId",
                        "stage",
                        "group",
                        "round",
                        "homeTeam",
                        "awayTeam",
                        "matchDate",
                        "status",
                        "score",
                        "prediction",
                        "pointsEarned"
                )
        );

        AggregationResults<MatchWithPrediction> results =
                mongoTemplate.aggregate(aggregation, "match", MatchWithPrediction.class);

        return new ArrayList<>(results.getMappedResults());
    }

    private Date startOfYear(int year) {
        return Date.from(LocalDate.of(year, 1, 1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

    @Override
    public int closeExpiredMatches(Date now) {
        Query query = new Query(Criteria.where("status").is("OPEN").and("matchDate").lte(now));
        Update update = new Update().set("status", "CLOSED");

        UpdateResult result = mongoTemplate.updateMulti(query, update, MatchDocument.class);
        return (int) result.getModifiedCount();
    }

    @Override
    public int findNextOpenRound(String competitionId) {
        Date now = new Date();

        Query nextOpenRoundQuery = new Query(new Criteria().andOperator(
                competitionCriteria(competitionId),
                Criteria.where("status").is("OPEN"),
                Criteria.where("matchDate").gte(now)
        ))
                .with(Sort.by(Sort.Direction.ASC, "matchDate"))
                .limit(1);

        MatchDocument nextOpenMatch = mongoTemplate.findOne(nextOpenRoundQuery, MatchDocument.class);

        if (nextOpenMatch != null) {
            return nextOpenMatch.getRound();
        }

        Query lastRoundQuery = new Query()
                .addCriteria(competitionCriteria(competitionId))
                .with(Sort.by(Sort.Direction.DESC, "round"))
                .limit(1);

        MatchDocument lastRoundMatch = mongoTemplate.findOne(lastRoundQuery, MatchDocument.class);
        if (lastRoundMatch != null) {
            return lastRoundMatch.getRound();
        }

        throw new IllegalStateException("No rounds found");
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
