package br.com.g12.response;

import br.com.g12.model.Match;
import br.com.g12.model.MatchWithPrediction;
import br.com.g12.model.Score;

import java.util.Date;

public record MatchResponse(
        String id,
        String competitionId,
        String stage,
        String group,
        int round,
        String homeTeam,
        String awayTeam,
        Date matchDate,
        Score score,
        Score prediction,
        Integer pointsEarned,
        String status
) {
    public static MatchResponse fromModel(Match match) {
        return new MatchResponse(match.getId(), match.getCompetitionId(), match.getStage(), match.getGroup(), match.getRound(), match.getHomeTeam(), match.getAwayTeam(), match.getMatchDate(), match.getScore(), null, null, match.getStatus());
    }

    public static MatchResponse fromModel(MatchWithPrediction match) {
        return new MatchResponse(match.getId(), match.getCompetitionId(), match.getStage(), match.getGroup(), match.getRound(), match.getHomeTeam(), match.getAwayTeam(), match.getMatchDate(), match.getScore(), match.getPrediction(), match.getPointsEarned(), match.getStatus());
    }

    public static MatchResponse fromModel(MatchWithPrediction match, boolean includePrediction) {
        return new MatchResponse(
                match.getId(),
                match.getCompetitionId(),
                match.getStage(),
                match.getGroup(),
                match.getRound(),
                match.getHomeTeam(),
                match.getAwayTeam(),
                match.getMatchDate(),
                match.getScore(),
                includePrediction ? match.getPrediction() : null,
                includePrediction ? match.getPointsEarned() : null,
                match.getStatus()
        );
    }
}

