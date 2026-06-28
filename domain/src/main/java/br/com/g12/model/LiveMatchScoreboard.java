package br.com.g12.model;

import java.util.Date;
import java.util.List;

public record LiveMatchScoreboard(
        String matchId,
        String homeTeam,
        String awayTeam,
        Score liveScore,
        Date updatedAt,
        List<LiveBetProjection> bets
) {}
