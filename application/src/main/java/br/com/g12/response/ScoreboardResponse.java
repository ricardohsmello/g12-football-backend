package br.com.g12.response;

import br.com.g12.model.Scoreboard;

public record ScoreboardResponse(
    String competitionId,
    int round,
    String username,
    int points,
    int year
) {
    public static ScoreboardResponse fromModel(Scoreboard model) {
        return new ScoreboardResponse(
            model.competitionId(),
            model.round(),
            model.username(),
            model.points(),
            model.year()
        );
    }
}
