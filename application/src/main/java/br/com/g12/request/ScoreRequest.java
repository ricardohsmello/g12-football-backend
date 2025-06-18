package br.com.g12.request;

import br.com.g12.model.Score;

public record ScoreRequest(
        int homeTeam,
        int awayTeam
) {
    public Score toModel() {
        return new Score(homeTeam, awayTeam);
    }
}
