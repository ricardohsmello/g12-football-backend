package br.com.g12.entity;

import br.com.g12.model.Scoreboard;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "scoreboard")
public class ScoreboardDocument {
    private String id;
    private int round;
    private String username;
    private int points;

    ScoreboardDocument() {

    }
    ScoreboardDocument(String id, int round, String username, int points) {
        this.id = id;
        this.round = round;
        this.username = username;
        this.points = points;
    }


    public Scoreboard toModel() {
        return new Scoreboard( id, round, username, points );
    }

    public static ScoreboardDocument fromModel(Scoreboard scoreboard) {
        return new ScoreboardDocument(
                scoreboard.id(),
                scoreboard.round(),
                scoreboard.username(),
                scoreboard.points()
        );
    }
}