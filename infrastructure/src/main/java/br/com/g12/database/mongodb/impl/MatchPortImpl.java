package br.com.g12.database.mongodb.impl;


import br.com.g12.database.mongodb.MatchRepository;
import br.com.g12.entity.BetDocument;
import br.com.g12.entity.MatchDocument;
import br.com.g12.model.Bet;
import br.com.g12.model.CompetitionDefaults;
import br.com.g12.model.Match;
import br.com.g12.model.MatchWithPrediction;
import br.com.g12.port.MatchPort;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
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
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Query matchQuery = new Query(new Criteria().andOperator(
                competitionCriteria(competitionId),
                Criteria.where("round").is(round),
                Criteria.where("matchDate").gte(startOfYear(year)).lt(startOfYear(year + 1))
        ));

        List<Match> matches = mongoTemplate.find(matchQuery, MatchDocument.class).stream()
                .map(MatchDocument::toModel)
                .toList();

        if (matches.isEmpty()) {
            return new ArrayList<>();
        }

        List<ObjectId> matchIds = matches.stream()
                .map(m -> new ObjectId(m.getId()))
                .toList();

        Query betQuery = new Query(Criteria.where("matchId").in(matchIds).and("username").is(username));
        Map<String, Bet> betsByMatchId = mongoTemplate.find(betQuery, BetDocument.class).stream()
                .map(BetDocument::toModel)
                .collect(Collectors.toMap(Bet::getMatchId, Function.identity(), (first, second) -> first));

        return matches.stream()
                .map(m -> {
                    Bet bet = betsByMatchId.get(m.getId());
                    return new MatchWithPrediction(
                            m.getId(),
                            m.getCompetitionId(),
                            m.getStage(),
                            m.getGroup(),
                            m.getRound(),
                            m.getHomeTeam(),
                            m.getAwayTeam(),
                            m.getMatchDate(),
                            m.getScore(),
                            bet != null ? bet.getPrediction() : null,
                            bet != null && bet.getPointsEarned() != null ? bet.getPointsEarned() : 0,
                            m.getStatus()
                    );
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Date startOfYear(int year) {
        return Date.from(LocalDate.of(year, 1, 1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

    @Override
    public List<Match> findExpiredOpenMatches(Date now) {
        Query query = new Query(Criteria.where("status").is("OPEN").and("matchDate").lte(now));
        return mongoTemplate.find(query, MatchDocument.class).stream()
                .map(MatchDocument::toModel)
                .toList();
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
