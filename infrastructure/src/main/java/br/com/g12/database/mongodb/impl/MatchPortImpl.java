package br.com.g12.database.mongodb.impl;


import br.com.g12.database.mongodb.MatchRepository;
import br.com.g12.entity.MatchDocument;
import br.com.g12.model.Match;
import br.com.g12.model.MatchWithPrediction;
import br.com.g12.port.MatchPort;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

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
    public List<Match> findByRound(int round) {
        List<MatchDocument> byRound = matchRepository.findByRound(round);
        return byRound.stream().map(MatchDocument::toModel).toList();
    }

    @Override
    public List<MatchWithPrediction> findByRoundUser(String username, int round) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("round").is(round)),
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

    @Override
    public int closeExpiredMatches(Date now) {
        Query query = new Query(Criteria.where("status").is("OPEN").and("matchDate").lt(now));
        Update update = new Update().set("status", "CLOSED");

        UpdateResult result = mongoTemplate.updateMulti(query, update, MatchDocument.class);
        return (int) result.getModifiedCount();
    }
}
