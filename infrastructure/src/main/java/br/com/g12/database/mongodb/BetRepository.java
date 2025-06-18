package br.com.g12.database.mongodb;

import br.com.g12.entity.BetDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends MongoRepository<BetDocument, String> {

    List<BetDocument> findByMatchId(ObjectId id);
    BetDocument findByMatchIdAndUsername(ObjectId matchId, String username);
}
