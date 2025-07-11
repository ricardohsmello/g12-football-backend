package br.com.g12.usecase.bet;

import br.com.g12.exception.ScoreException;
import br.com.g12.model.Bet;
import br.com.g12.model.Match;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.BetPort;
import br.com.g12.port.MatchPort;
import br.com.g12.port.ScoreboardPort;
import br.com.g12.service.ScoringService;
import br.com.g12.usecase.AbstractUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreBetsUseCase extends AbstractUseCase<Integer> {

    private final MatchPort matchPort;
    private final BetPort betPort;
    private final ScoreboardPort scoreboardPort;
    private final ScoringService scoringService;

    public ScoreBetsUseCase(MatchPort matchPort,
                            BetPort betPort,
                            ScoreboardPort scoreboardPort,
                            ScoringService scoringService) {
        this.matchPort = matchPort;
        this.betPort = betPort;
        this.scoreboardPort = scoreboardPort;
        this.scoringService = scoringService;
    }

    public void execute(int round) {
        long total = System.currentTimeMillis();
        logInput(round);
        long start = System.currentTimeMillis();
        try {
            log.info("Starting validations");
            validateRoundIsNotClosed(round);

            List<Match> matches = matchPort.findByRound(round);
            validateAllMatchesAreClosed(matches);

            List<Bet> allBets = betPort.findByRound(round);
            Map<String, Match> matchesById = getMatchesWithScoreById(matches);

            log.info("Finished validations. Took {} s", (System.currentTimeMillis() - start) / 1000);
            List<Bet> scoredBets = calculatePointsForBets(allBets, matchesById);

            long sav = System.currentTimeMillis();
            log.info("Starting saving all scoreBets");
            betPort.saveAll(scoredBets);
            log.info("Finished saving all scoreBets in {} s", System.currentTimeMillis() - sav / 1000 );

            closeAllMatchesIfNeeded(matches);

            saveScoreboardForRound(round, scoredBets);

            log.info("Finished validations. Took {} s", (System.currentTimeMillis() - total) / 1000);
        } catch (ScoreException e) {
            logError(e);
            throw e;
        }
    }

    private void validateRoundIsNotClosed(int round) {
        if (!scoreboardPort.findByRound(round).isEmpty()) {
            throw new ScoreException("Round " + round + " has already been executed!");
        }
    }

    private void validateAllMatchesAreClosed(List<Match> matches) {
        boolean hasOpenMatch = matches.stream().anyMatch(m -> "OPEN".equals(m.getStatus()));
        if (hasOpenMatch) {
            throw new ScoreException("You can't settle the round with OPEN matches.");
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

                int points = scoringService.calculate(match, bet, otherBets);
                bet.setPointsEarned(points);
                scoredBets.add(bet);
            }
        }

        log.info("Finished calculatePointsForBets took {} s", (System.currentTimeMillis() - start) / 1000);
        return scoredBets;
    }

    private void closeAllMatchesIfNeeded(List<Match> matches) {
        for (Match match : matches) {
            if (!"CLOSED".equals(match.getStatus())) {
                match.setStatus("CLOSED");
                matchPort.save(match);
            }
        }
    }

    private void saveScoreboardForRound(int round, List<Bet> bets) {
        long start = System.currentTimeMillis();

        log.info("starting saveScoreboardForRound");
        Map<String, Integer> roundScores = computeUserScores(bets);

        List<Scoreboard> roundScoreboard = toScoreboardList(round, roundScores);
        scoreboardPort.saveAll(roundScoreboard);

        log.info("Finished saveScoreboardForRound took {} s", (System.currentTimeMillis() - start) / 1000);

        updateTotalScoreboard(roundScores);


    }

    private Map<String, Integer> computeUserScores(List<Bet> bets) {
        return bets.stream()
                .collect(Collectors.groupingBy(
                        Bet::getUsername,
                        Collectors.summingInt(Bet::getPointsEarned)
                ));
    }

    private List<Scoreboard> toScoreboardList(int round, Map<String, Integer> userScores) {
        return userScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> new Scoreboard(null, round, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private void updateTotalScoreboard(Map<String, Integer> roundScores) {
        long start = System.currentTimeMillis();
        log.info("starting updateTotalScoreboard");

        List<String> usernames = new ArrayList<>(roundScores.keySet());
        List<Scoreboard> existingTotals = scoreboardPort.findByRoundAndUsernames(0, usernames);

        Map<String, Scoreboard> totalByUser = existingTotals.stream()
                .collect(Collectors.toMap(Scoreboard::username, s -> s));

        List<Scoreboard> updatedTotals = roundScores.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    int roundPoints = entry.getValue();
                    Scoreboard existing = totalByUser.get(username);

                    return (existing == null)
                            ? new Scoreboard(null, 0, username, roundPoints)
                            : new Scoreboard(existing.id(), 0, username, existing.points() + roundPoints);
                })
                .toList();

        scoreboardPort.saveAll(updatedTotals);
        log.info("Finished updateTotalScoreboard took {} s", (System.currentTimeMillis() - start) / 1000);
    }
}
