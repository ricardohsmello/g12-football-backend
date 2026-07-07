package br.com.g12.usecase.live;

import br.com.g12.model.*;
import br.com.g12.port.BetPort;
import br.com.g12.port.LiveMatchScorePort;
import br.com.g12.port.MatchPort;
import br.com.g12.port.ScoreboardPort;
import br.com.g12.service.PredictionScoringService;
import br.com.g12.usecase.AbstractUseCase;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetLiveScoreboardUseCase extends AbstractUseCase<List<LiveMatchScoreboard>> {

    private static final int GENERAL_SCOREBOARD_ROUND = 0;

    private final MatchPort matchPort;
    private final BetPort betPort;
    private final LiveMatchScorePort liveMatchScorePort;
    private final ScoreboardPort scoreboardPort;
    private final PredictionScoringService predictionScoringService;

    public GetLiveScoreboardUseCase(MatchPort matchPort,
                                    BetPort betPort,
                                    LiveMatchScorePort liveMatchScorePort,
                                    ScoreboardPort scoreboardPort,
                                    PredictionScoringService predictionScoringService) {
        this.matchPort = matchPort;
        this.betPort = betPort;
        this.liveMatchScorePort = liveMatchScorePort;
        this.scoreboardPort = scoreboardPort;
        this.predictionScoringService = predictionScoringService;
    }

    public List<LiveMatchScoreboard> execute(String competitionId, int round) {
        String resolvedCompetitionId = CompetitionDefaults.competitionIdOrDefault(competitionId);
        List<Match> openMatches = matchPort.findByCompetitionIdAndRoundAndStatus(resolvedCompetitionId, 5, "CLOSED");

        if (openMatches.isEmpty()) {
            return List.of();
        }

        List<String> matchIds = openMatches.stream().map(Match::getId).toList();

        Map<String, LiveMatchScore> liveScoresByMatchId = liveMatchScorePort.findByMatchIdIn(matchIds)
                .stream()
                .collect(Collectors.toMap(LiveMatchScore::getMatchId, s -> s));

        List<Match> liveMatches = openMatches.stream()
                .filter(m -> liveScoresByMatchId.containsKey(m.getId()))
                .toList();

        if (liveMatches.isEmpty()) {
            return List.of();
        }

        List<Bet> allBets = betPort.findByMatchIdIn(matchIds);

        Map<String, List<Bet>> betsByMatchId = allBets.stream()
                .collect(Collectors.groupingBy(Bet::getMatchId));

        record RawProjection(String username, Score prediction, int projectedPoints) {}

        Map<String, List<RawProjection>> projectionsByMatchId = new HashMap<>();
        Map<String, Integer> projectedRoundPointsByUsername = new HashMap<>();

        for (Match match : liveMatches) {
            LiveMatchScore liveScore = liveScoresByMatchId.get(match.getId());
            Match matchWithLiveScore = new Match(
                    match.getId(), match.getCompetitionId(), match.getStage(), match.getGroup(),
                    match.getRound(), match.getHomeTeam(), match.getAwayTeam(),
                    match.getMatchDate(), liveScore.getScore(), match.getStatus()
            );

            List<Bet> matchBets = betsByMatchId.getOrDefault(match.getId(), List.of());
            List<RawProjection> projections = matchBets.stream()
                    .map(bet -> {
                        List<Bet> otherBets = matchBets.stream()
                                .filter(b -> !b.getId().equals(bet.getId()))
                                .toList();
                        int points = predictionScoringService.calculate(matchWithLiveScore, bet, otherBets);
                        return new RawProjection(bet.getUsername(), bet.getPrediction(), points);
                    })
                    .toList();

            projections.forEach(p ->
                    projectedRoundPointsByUsername.merge(p.username(), p.projectedPoints(), Integer::sum));
            projectionsByMatchId.put(match.getId(), projections);
        }

        Map<String, Integer> currentTotalPointsByUsername = scoreboardPort
                .findByCompetitionIdAndRoundAndYear(resolvedCompetitionId, GENERAL_SCOREBOARD_ROUND, LocalDate.now().getYear())
                .stream()
                .collect(Collectors.toMap(Scoreboard::username, Scoreboard::points, (a, b) -> a));

        return liveMatches.stream()
                .map(match -> {
                    LiveMatchScore liveScore = liveScoresByMatchId.get(match.getId());
                    List<LiveBetProjection> bets = projectionsByMatchId.get(match.getId()).stream()
                            .map(p -> {
                                int currentTotal = currentTotalPointsByUsername.getOrDefault(p.username(), 0);
                                int projectedTotal = currentTotal
                                        + projectedRoundPointsByUsername.getOrDefault(p.username(), 0);
                                return new LiveBetProjection(
                                        p.username(), p.prediction(), p.projectedPoints(),
                                        currentTotal, projectedTotal);
                            })
                            .toList();

                    return new LiveMatchScoreboard(
                            match.getId(),
                            match.getHomeTeam(),
                            match.getAwayTeam(),
                            liveScore.getScore(),
                            liveScore.getUpdatedAt(),
                            bets
                    );
                })
                .toList();
    }
}
