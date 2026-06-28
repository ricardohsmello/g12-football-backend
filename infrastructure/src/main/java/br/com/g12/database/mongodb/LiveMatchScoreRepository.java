package br.com.g12.database.mongodb;

import br.com.g12.entity.LiveMatchScoreDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveMatchScoreRepository extends MongoRepository<LiveMatchScoreDocument, String> {
    Optional<LiveMatchScoreDocument> findByMatchId(ObjectId matchId);
    List<LiveMatchScoreDocument> findByMatchIdIn(List<ObjectId> matchIds);
    void deleteByMatchId(ObjectId matchId);
}
