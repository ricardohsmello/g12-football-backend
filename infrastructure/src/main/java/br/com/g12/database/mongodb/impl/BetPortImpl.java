package br.com.g12.database.mongodb.impl;

import br.com.g12.database.mongodb.BetRepository;
import br.com.g12.entity.BetDocument;
import br.com.g12.model.Bet;
import br.com.g12.model.CompetitionDefaults;
import br.com.g12.port.BetPort;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class BetPortImpl implements BetPort {

    private final BetRepository betRepository;
    private final MongoTemplate mongoTemplate;

    public BetPortImpl(BetRepository betRepository, MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
        this.betRepository = betRepository;
    }

    @Override
    public Bet save(Bet bet) {
        BetDocument existing = betRepository.findByMatchIdAndUsername(new ObjectId(bet.getMatchId()), bet.getUsername());

        if (existing != null) {
            existing.setPrediction(bet.getPrediction());
            existing.setPointsEarned(bet.getPointsEarned());

            return betRepository.save(existing).toModel();
        }

        return betRepository.save(BetDocument.fromModel(bet)).toModel();
    }

    @Override
    public void saveAll(List<Bet> bets) {
        MongoCollection<Document> collection = mongoTemplate
                .getCollection(mongoTemplate.getCollectionName(BetDocument.class));

        for (Bet bet : bets) {
            Document updateDoc = new Document();
            mongoTemplate.getConverter().write(BetDocument.fromModel(bet), updateDoc);

            Object id = updateDoc.remove("_id");

            collection.updateOne(
                    Filters.eq("_id", id),
                    new Document("$set", updateDoc),
                    new UpdateOptions().upsert(true)
            );
        }
    }

    @Override
    public List<Bet> findByMatchIdInAndPointsEarnedIsNull(List<String> matchIds) {
        List<ObjectId> objectIds = matchIds.stream()
                .map(ObjectId::new)
                .toList();

        return betRepository.findByMatchIdInAndPointsEarnedIsNull(objectIds).stream().map(BetDocument::toModel).toList();
    }

    @Override
    public List<Bet> findByMatchIdIn(List<String> matchIds) {
        List<ObjectId> objectIds = matchIds.stream()
                .map(ObjectId::new)
                .toList();

        return betRepository.findByMatchIdIn(objectIds).stream().map(BetDocument::toModel).toList();
    }

    @Override
    public int countDistinctUsernamesByCompetitionIdAndRound(String competitionId, int round) {
        int currentYear = LocalDate.now().getYear();

        return mongoTemplate.query(BetDocument.class)
                .distinct("username")
                .matching(query(new Criteria().andOperator(
                        competitionCriteria(competitionId),
                        where("round").is(round),
                        where("date").gte(startOfYear(currentYear)).lt(startOfYear(currentYear + 1))
                )))
                .all()
                .size();
    }

    private Criteria competitionCriteria(String competitionId) {
        String normalizedCompetitionId = CompetitionDefaults.competitionIdOrDefault(competitionId);
        Criteria currentCompetition = where("competitionId").is(normalizedCompetitionId);

        if (!CompetitionDefaults.DEFAULT_COMPETITION_ID.equals(normalizedCompetitionId)) {
            return currentCompetition;
        }

        return new Criteria().orOperator(
                currentCompetition,
                where("competitionId").exists(false),
                where("competitionId").is(null)
        );
    }

    private Date startOfYear(int year) {
        return Date.from(LocalDate.of(year, 1, 1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

}
