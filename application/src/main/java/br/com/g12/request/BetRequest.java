package br.com.g12.request;

import br.com.g12.model.Bet;
import br.com.g12.model.Score;

import java.util.Date;

public record BetRequest(
        String matchId,
        String username,
        Score prediction,
        int round
) {
    public Bet toModel() {
        return new Bet(null, matchId, username, prediction, round, null, new Date());
    }
}
