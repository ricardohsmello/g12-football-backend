package br.com.g12.database.mongodb;

import br.com.g12.entity.ScoreboardDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreboardRepository extends MongoRepository<ScoreboardDocument, String> {

    List<ScoreboardDocument> findByRoundOrderByPointsDesc(int round);

    Optional<ScoreboardDocument> findByRoundAndUsername(int round, String username);
}
