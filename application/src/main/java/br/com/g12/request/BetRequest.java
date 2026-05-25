package br.com.g12.request;

import br.com.g12.model.Bet;
import br.com.g12.model.Score;

import java.util.Date;

public record BetRequest(
        String competitionId,
        String matchId,
        String username,
        Score prediction,
        int round
) {
    public BetRequest(String matchId, String username, Score prediction, int round) {
        this(null, matchId, username, prediction, round);
    }

    public Bet toModel() {
        return new Bet(null, competitionId, matchId, username, prediction, round, null, new Date());
    }
}
