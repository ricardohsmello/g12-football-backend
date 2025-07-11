package br.com.g12.database.mongodb.impl;

import br.com.g12.database.mongodb.BetRepository;
import br.com.g12.entity.BetDocument;
import br.com.g12.model.Bet;
import br.com.g12.port.BetPort;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class BetPortImpl implements BetPort {

    private final BetRepository betRepository;
    private final MongoTemplate mongoTemplate;

    BetPortImpl(BetRepository betRepository, MongoTemplate mongoTemplate)
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
        List<BetDocument> documents = bets.stream()
                .map(BetDocument::fromModel)
                .toList();
        MongoCollection<Document> collection = mongoTemplate
                .getCollection(mongoTemplate.getCollectionName(BetDocument.class));

        List<WriteModel<Document>> operations = new ArrayList<>();

        for (BetDocument doc : documents) {
            Document updateDoc = new Document();
            mongoTemplate.getConverter().write(doc, updateDoc);

            Object id = updateDoc.get("_id");
            updateDoc.remove("_id");

            Document setDoc = new Document("$set", updateDoc);

            UpdateOneModel<Document> model = new UpdateOneModel<>(
                    Filters.eq("_id", id),
                    setDoc,
                    new UpdateOptions().upsert(true)
            );

            operations.add(model);
        }

        if (!operations.isEmpty()) {
            collection.bulkWrite(operations, new BulkWriteOptions().ordered(false));
        }
    }

    @Override
    public List<Bet> findByRound(int round) {
        return betRepository.findByRound(round).stream().map(BetDocument::toModel).toList();
    }

    @Override
    public Bet findById(String id) {
        Optional<BetDocument> byId = betRepository.findById(id);
        return byId.map(BetDocument::toModel).orElse(null);
    }

    @Override
    public List<Bet> findByMatchId(String id) {
        List<BetDocument> byMatchId = betRepository.findByMatchId(new ObjectId(id));
        return byMatchId.stream().map(BetDocument::toModel).toList();
    }

    @Override
    public int countDistinctUsernamesByRound(int round) {
        Aggregation agg = Aggregation.newAggregation(
                match(Criteria.where("round").is(round)),
                group("username"),
                count().as("total")
        );

        AggregationResults<Document> result = mongoTemplate.aggregate(agg, "bet", Document.class);
        Document doc = result.getUniqueMappedResult();

        return doc.getInteger("total", 0);
    }

}
