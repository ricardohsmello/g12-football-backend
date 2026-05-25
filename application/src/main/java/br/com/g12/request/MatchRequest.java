package br.com.g12.request;

import br.com.g12.model.Match;

import java.util.Date;

public record MatchRequest(
        String competitionId,
        String stage,
        String group,
        int round,
        String homeTeam,
        String awayTeam,
        Date matchDate,
        String status
) {
    public MatchRequest(int round, String homeTeam, String awayTeam, Date matchDate, String status) {
        this(null, null, null, round, homeTeam, awayTeam, matchDate, status);
    }

    public Match toModel() {
        return new Match(null, competitionId, stage, group, round, homeTeam, awayTeam, matchDate, null, status);
    }
}

