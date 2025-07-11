package br.com.g12.database.mongodb.impl;

import br.com.g12.database.mongodb.BetRepository;
import br.com.g12.entity.BetDocument;
import br.com.g12.model.Bet;
import br.com.g12.port.BetPort;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

        List<ReplaceOneModel<Document>> operations = documents.stream()
                .map(doc -> {
                    Document bsonDoc = new Document();
                    mongoTemplate.getConverter().write(doc, bsonDoc);
                    bsonDoc.remove("_id");

                    Bson filter = Filters.eq("_id", doc.getId());

                    ReplaceOptions options = new ReplaceOptions().upsert(true);
                    return new ReplaceOneModel<>(filter, bsonDoc, options);
                })
                .toList();

        String collectionName = mongoTemplate.getCollectionName(BetDocument.class);
        MongoCollection<Document> collection = mongoTemplate.getDb().getCollection(collectionName);

        collection.bulkWrite(operations, new BulkWriteOptions().ordered(false));
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

}
