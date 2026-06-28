package br.com.g12.database.mongodb.impl;

import br.com.g12.database.mongodb.LiveMatchScoreRepository;
import br.com.g12.entity.LiveMatchScoreDocument;
import br.com.g12.model.LiveMatchScore;
import br.com.g12.port.LiveMatchScorePort;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LiveMatchScorePortImpl implements LiveMatchScorePort {

    private final LiveMatchScoreRepository repository;

    public LiveMatchScorePortImpl(LiveMatchScoreRepository repository) {
        this.repository = repository;
    }

    @Override
    public LiveMatchScore save(LiveMatchScore liveMatchScore) {
        return repository.save(LiveMatchScoreDocument.fromModel(liveMatchScore)).toModel();
    }

    @Override
    public Optional<LiveMatchScore> findByMatchId(String matchId) {
        return repository.findByMatchId(new ObjectId(matchId)).map(LiveMatchScoreDocument::toModel);
    }

    @Override
    public List<LiveMatchScore> findByMatchIdIn(List<String> matchIds) {
        List<ObjectId> objectIds = matchIds.stream().map(ObjectId::new).toList();
        return repository.findByMatchIdIn(objectIds).stream()
                .map(LiveMatchScoreDocument::toModel)
                .toList();
    }

    @Override
    public void deleteByMatchId(String matchId) {
        repository.deleteByMatchId(new ObjectId(matchId));
    }
}
