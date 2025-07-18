package br.com.g12.database.mongodb;

import br.com.g12.entity.MatchDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends MongoRepository<MatchDocument, String> {

    List<MatchDocument> findByRound(int round);

    @Aggregation(pipeline = {
            "{ $facet: { " +
                    "futureRounds: [" +
                    "{ $match: { matchDate: { $gte: ?0 } } }," +
                    "{ $sort: { round: 1 } }," +
                    "{ $limit: 1 }" +
                    "]," +
                    "lastRound: [" +
                    "{ $sort: { round: -1 } }," +
                    "{ $limit: 1 }" +
                    "]" +
                    "} }",
            "{ $project: {" +
                    "round: {" +
                    "$cond: [" +
                    "{ $gt: [ { $size: '$futureRounds' }, 0 ] }," +
                    "{ $arrayElemAt: [ '$futureRounds.round', 0 ] }," +
                    "{ $arrayElemAt: [ '$lastRound.round', 0 ] }" +
                    "]" +
                    "}" +
                    "} }"
    })
    Optional<Integer> findNextMatchRound(Date now);
}
