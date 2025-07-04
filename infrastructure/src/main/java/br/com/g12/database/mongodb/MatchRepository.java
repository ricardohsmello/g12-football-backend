package br.com.g12.database.mongodb;

import br.com.g12.entity.MatchDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends MongoRepository<MatchDocument, String> {

    List<MatchDocument> findByRound(int round);

    @Aggregation(pipeline = {
            "{ '$match': { 'status': 'OPEN' } }",
            "{ '$group': { '_id': '$round'} }",
            "{ '$sort': { '_id': 1 } }",
            "{ '$limit': 1  }"
    })
    Optional<Integer> findNextOpenRound();
}
