package br.com.g12.entity;

import br.com.g12.model.Scoreboard;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "scoreboard")
@CompoundIndex(name = "competitionRoundYearAndUsername", def = "{'competitionId': 1, 'round': 1, 'year': 1, 'username': 1}")
public class ScoreboardDocument {
    private String id;
    private String competitionId;
    private int round;
    private String username;
    private int points;
    private int year;
    private List<String> prediction;

    ScoreboardDocument() {
    }

    ScoreboardDocument(String id, String competitionId, int round, String username, int points, int year, List<String> prediction) {
        this.id = id;
        this.competitionId = competitionId;
        this.round = round;
        this.username = username;
        this.points = points;
        this.year = year;
        this.prediction = prediction;
    }

    public Scoreboard toModel() {
        return new Scoreboard( id, competitionId, round, username, points, year, prediction );
    }

    public static ScoreboardDocument fromModel(Scoreboard scoreboard) {
        return new ScoreboardDocument(
                scoreboard.id(),
                scoreboard.competitionId(),
                scoreboard.round(),
                scoreboard.username(),
                scoreboard.points(),
                scoreboard.year(),
                scoreboard.prediction()
        );
    }
}
