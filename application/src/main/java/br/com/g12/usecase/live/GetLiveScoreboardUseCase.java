package br.com.g12.usecase.live;

import br.com.g12.model.*;
import br.com.g12.port.BetPort;
import br.com.g12.port.LiveMatchScorePort;
import br.com.g12.port.MatchPort;
import br.com.g12.service.PredictionScoringService;
import br.com.g12.usecase.AbstractUseCase;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetLiveScoreboardUseCase extends AbstractUseCase<List<LiveMatchScoreboard>> {

    private final MatchPort matchPort;
    private final BetPort betPort;
    private final LiveMatchScorePort liveMatchScorePort;
    private final PredictionScoringService predictionScoringService;

    public GetLiveScoreboardUseCase(MatchPort matchPort,
                                    BetPort betPort,
                                    LiveMatchScorePort liveMatchScorePort,
                                    PredictionScoringService predictionScoringService) {
        this.matchPort = matchPort;
        this.betPort = betPort;
        this.liveMatchScorePort = liveMatchScorePort;
        this.predictionScoringService = predictionScoringService;
    }

    public List<LiveMatchScoreboard> execute(String competitionId, int round) {
        List<Match> openMatches = matchPort.findByCompetitionIdAndRoundAndStatus(
                CompetitionDefaults.competitionIdOrDefault(competitionId), round, "CLOSED");

        if (openMatches.isEmpty()) {
            return List.of();
        }

        List<String> matchIds = openMatches.stream().map(Match::getId).toList();

        Map<String, LiveMatchScore> liveScoresByMatchId = liveMatchScorePort.findByMatchIdIn(matchIds)
                .stream()
                .collect(Collectors.toMap(LiveMatchScore::getMatchId, s -> s));

        List<Bet> allBets = betPort.findByMatchIdIn(matchIds);

        Map<String, List<Bet>> betsByMatchId = allBets.stream()
                .collect(Collectors.groupingBy(Bet::getMatchId));

        return openMatches.stream()
                .filter(m -> liveScoresByMatchId.containsKey(m.getId()))
                .map(match -> {
                    LiveMatchScore liveScore = liveScoresByMatchId.get(match.getId());
                    Match matchWithLiveScore = new Match(
                            match.getId(), match.getCompetitionId(), match.getStage(), match.getGroup(),
                            match.getRound(), match.getHomeTeam(), match.getAwayTeam(),
                            match.getMatchDate(), liveScore.getScore(), match.getStatus()
                    );

                    List<Bet> matchBets = betsByMatchId.getOrDefault(match.getId(), List.of());
                    List<LiveBetProjection> projections = matchBets.stream()
                            .map(bet -> {
                                List<Bet> otherBets = matchBets.stream()
                                        .filter(b -> !b.getId().equals(bet.getId()))
                                        .toList();
                                int points = predictionScoringService.calculate(matchWithLiveScore, bet, otherBets);
                                return new LiveBetProjection(bet.getUsername(), bet.getPrediction(), points);
                            })
                            .toList();

                    return new LiveMatchScoreboard(
                            match.getId(),
                            match.getHomeTeam(),
                            match.getAwayTeam(),
                            liveScore.getScore(),
                            liveScore.getUpdatedAt(),
                            projections
                    );
                })
                .toList();
    }
}
