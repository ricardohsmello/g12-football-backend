package br.com.g12.usecase.bet;

import br.com.g12.exception.ScoreException;
import br.com.g12.model.Bet;
import br.com.g12.model.Match;
import br.com.g12.port.BetPort;
import br.com.g12.port.MatchPort;
import br.com.g12.service.PredictionScoringService;
import br.com.g12.service.RoundScoreboardService;
import br.com.g12.usecase.AbstractUseCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreBetsUseCase extends AbstractUseCase<Integer> {

    private final MatchPort matchPort;
    private final BetPort betPort;
    private final PredictionScoringService predictionScoringService;
    private final RoundScoreboardService roundScoreboardService;

    public ScoreBetsUseCase(MatchPort matchPort,
                            BetPort betPort,
                            PredictionScoringService predictionScoringService,
                            RoundScoreboardService roundScoreboardService) {
        this.matchPort = matchPort;
        this.betPort = betPort;
        this.predictionScoringService = predictionScoringService;
        this.roundScoreboardService = roundScoreboardService;
    }

    public void execute(int round) {
        try {
            logInput(round);
            var total = System.currentTimeMillis();

            List<Match> matches = matchPort.findByRoundAndStatus(round, "CLOSED");

            List<String> matchIds = matches.stream()
                    .map(Match::getId)
                    .toList();

            List<Bet> allBets = betPort.findByMatchIdInAndPointsEarnedIsNull(matchIds);

            if (allBets.isEmpty()) {
                log.info("No bets found for round {}. Skipping score.", round);
                return;
            }

            Map<String, Match> matchesById = getMatchesWithScoreById(matches);
            List<Bet> scoredBets = calculatePointsForBets(allBets, matchesById);

            betPort.saveAll(scoredBets);

            closeAllMatchesIfNeeded(matches);

            roundScoreboardService.execute(scoredBets, round);

            log.info("Finished Score bets use case. Took {} s", (System.currentTimeMillis() - total) / 1000);
        } catch (ScoreException e) {
            logError(e);
            throw e;
        }
    }

    private Map<String, Match> getMatchesWithScoreById(List<Match> matches) {
        return matches.stream()
                .filter(m -> m.getScore() != null)
                .collect(Collectors.toMap(Match::getId, m -> m));
    }

    private List<Bet> calculatePointsForBets(List<Bet> allBets, Map<String, Match> matchesById) {
        long start = System.currentTimeMillis();
        log.info("Starting calculatePointsForBets");

        Map<String, List<Bet>> betsGroupedByMatch = allBets.stream()
                .collect(Collectors.groupingBy(Bet::getMatchId));

        List<Bet> scoredBets = new ArrayList<>();

        for (Map.Entry<String, List<Bet>> entry : betsGroupedByMatch.entrySet()) {
            Match match = matchesById.get(entry.getKey());
            if (match == null) continue;

            List<Bet> bets = entry.getValue();
            for (Bet bet : bets) {
                List<Bet> otherBets = bets.stream()
                        .filter(b -> !b.getId().equals(bet.getId()))
                        .toList();

                int points = predictionScoringService.calculate(match, bet, otherBets);
                bet.setPointsEarned(points);
                scoredBets.add(bet);
            }
        }

        log.info("Finished calculatePointsForBets took {} s", (System.currentTimeMillis() - start) / 1000);
        return scoredBets;
    }

    private void closeAllMatchesIfNeeded(List<Match> matches) {
        Date now = new Date();
        for (Match match : matches) {
            if (match.getMatchDate().before(now) && !"CLOSED".equals(match.getStatus())) {
                match.setStatus("CLOSED");
                matchPort.save(match);
            }
        }
    }
}
