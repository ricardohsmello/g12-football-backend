package br.com.g12.response;

import br.com.g12.model.Scoreboard;

public record ScoreboardResponse(
    String username,
    int points
) {
    public static ScoreboardResponse fromModel(Scoreboard model) {
        return new ScoreboardResponse(
            model.username(),
            model.points()
        );
    }
}