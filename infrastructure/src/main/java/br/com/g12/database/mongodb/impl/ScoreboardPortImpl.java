package br.com.g12.database.mongodb.impl;

import br.com.g12.database.mongodb.ScoreboardRepository;
import br.com.g12.entity.ScoreboardDocument;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.ScoreboardPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScoreboardPortImpl implements ScoreboardPort {

    private final ScoreboardRepository repository;

    public ScoreboardPortImpl(ScoreboardRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Scoreboard> findByRoundAndYear(int round, int year) {
        return repository.findByRoundAndYearOrderByPointsDesc(round, year).stream()
                .map(ScoreboardDocument::toModel)
                .toList();
    }

    @Override
    public void saveAll(List<Scoreboard> scoreboards) {

        List<ScoreboardDocument> documents = scoreboards.stream()
                .map(ScoreboardDocument::fromModel)
                .toList();

       repository.saveAll(documents);
    }

    @Override
    public Scoreboard findByRoundAndYearAndUsername(int round, int year, String username) {
        return repository.findByRoundAndYearAndUsername(round, year, username).map(ScoreboardDocument::toModel).orElse(null);
    }

    @Override
    public List<Scoreboard> findByRoundAndYearAndUsernames(int round, int year, List<String> usernames) {
        List<ScoreboardDocument> documents = repository.findByRoundAndYearAndUsernameIn(round, year, usernames);
        return documents.stream()
                .map(ScoreboardDocument::toModel)
                .toList();
    }

    @Override
    public void save(Scoreboard scoreboard) {
        repository.save(ScoreboardDocument.fromModel(scoreboard));
    }
}
