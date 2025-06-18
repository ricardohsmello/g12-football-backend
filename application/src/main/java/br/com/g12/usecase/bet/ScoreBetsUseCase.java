package br.com.g12.usecase.bet;

import br.com.g12.model.Bet;
import br.com.g12.model.Match;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.BetPort;
import br.com.g12.port.MatchPort;
import br.com.g12.port.ScoreboardPort;
import br.com.g12.service.ScoringService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ScoreBetsUseCase {

    private final MatchPort matchPort;
    private final BetPort betPort;
    private final ScoringService scoringService;
    private final ScoreboardPort scoreboardPort;

    public ScoreBetsUseCase(final MatchPort matchPort, final BetPort betPort, ScoreboardPort scoreboardPort, ScoringService scoringService) {
        this.matchPort = matchPort;
        this.betPort = betPort;
        this.scoreboardPort = scoreboardPort;
        this.scoringService = scoringService;
    }

    public void execute(int round) {
        List<Match> matches = matchPort.findByRound(round);
        List<Bet> allBetsOfRound = new ArrayList<>();

        for (Match match : matches) {
            //TODO VALIDATORS

            if (match.getScore() == null) continue;

            List<Bet> bets = betPort.findByMatchId(match.getId());

            for (Bet bet : bets) {
                List<Bet> otherBets = bets.stream()
                        .filter(b -> !b.getId().equals(bet.getId()))
                        .toList();

                int points = scoringService.calculate(match, bet, otherBets);
                bet.setPointsEarned(points);
                betPort.save(bet);

                match.setStatus("CLOSED");
                matchPort.save(match);
                allBetsOfRound.add(bet);
            }
        }

        calculateScoreBoard(round, allBetsOfRound);
    }

    private void calculateScoreBoard(int round, List<Bet> allBetsOfRound) {
        Map<String, Integer> userScores = allBetsOfRound.stream()
                .collect(Collectors.groupingBy(
                        Bet::getUsername,
                        Collectors.summingInt(Bet::getPointsEarned)
                ));

        List<Scoreboard> scoreboard = new ArrayList<>();
        AtomicInteger position = new AtomicInteger(1);

        userScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    scoreboard.add(new Scoreboard(
                            null,
                            round,
                            entry.getKey(),
                            entry.getValue()
                    ));
                });

        scoreboardPort.saveAll(scoreboard);
        updateUserTotalScoreboard(userScores);
    }

    private void updateUserTotalScoreboard(Map<String, Integer> roundUserScores) {
        for (Map.Entry<String, Integer> entry : roundUserScores.entrySet()) {
            String username = entry.getKey();
            int pointsThisRound = entry.getValue();

            Scoreboard currentTotal = scoreboardPort.findByRoundAndUsername(0, username);

            if (currentTotal == null) {
                Scoreboard newTotal = new Scoreboard(null, 0, username, pointsThisRound);
                scoreboardPort.save(newTotal);
            } else {
                int newTotalPoints = currentTotal.points() + pointsThisRound;
                Scoreboard updated = new Scoreboard(currentTotal.id(), 0, username, newTotalPoints);
                scoreboardPort.save(updated);
            }
        }
    }
}
