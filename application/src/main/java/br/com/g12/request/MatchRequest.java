package br.com.g12.request;

import br.com.g12.model.Match;

import java.util.Date;

public record MatchRequest(
        int round,
        String homeTeam,
        String awayTeam,
        Date matchDate,
        String status
) {
    public Match toModel() {
        return new Match(null, round, homeTeam, awayTeam, matchDate, null, status);
    }
}

