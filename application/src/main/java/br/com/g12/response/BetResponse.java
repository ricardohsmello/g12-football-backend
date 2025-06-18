package br.com.g12.response;

import br.com.g12.model.Bet;
import br.com.g12.model.Score;

public record BetResponse(
        String id,
        String matchId,
        String username,
        Score prediction,
        int round
) {
    public static BetResponse fromModel(Bet bet) {
        return new BetResponse(bet.getId(), bet.getMatchId(), bet.getUsername(), bet.getPrediction(), bet.getRound());
    }
}