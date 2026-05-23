package br.com.g12.entity;

import br.com.g12.model.Scoreboard;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "scoreboard")
@CompoundIndex(name = "roundYearAndUsername", def = "{'round': 1, 'year': 1, 'username': 1}")
public class ScoreboardDocument {
    private String id;
    private int round;
    private String username;
    private int points;
    private int year;

    ScoreboardDocument() {
    }

    ScoreboardDocument(String id, int round, String username, int points, int year) {
        this.id = id;
        this.round = round;
        this.username = username;
        this.points = points;
        this.year = year;
    }

    public Scoreboard toModel() {
        return new Scoreboard( id, round, username, points, year );
    }

    public static ScoreboardDocument fromModel(Scoreboard scoreboard) {
        return new ScoreboardDocument(
                scoreboard.id(),
                scoreboard.round(),
                scoreboard.username(),
                scoreboard.points(),
                scoreboard.year()
        );
    }
}
