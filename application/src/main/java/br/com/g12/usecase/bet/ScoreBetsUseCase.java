package br.com.g12.usecase.bet;

import br.com.g12.port.BetPort;
import br.com.g12.port.MatchPort;
import br.com.g12.port.ScoreboardPort;
import br.com.g12.service.PredictionScoringService;
import br.com.g12.usecase.AbstractUseCase;

public class ScoreBetsUseCase extends AbstractUseCase<Integer> {

    private final MatchPort matchPort;
    private final BetPort betPort;
    private final ScoreboardPort scoreboardPort;
    private final PredictionScoringService predictionScoringService;

    public ScoreBetsUseCase(MatchPort matchPort,
                            BetPort betPort,
                            ScoreboardPort scoreboardPort,
                            PredictionScoringService predictionScoringService) {
        this.matchPort = matchPort;
        this.betPort = betPort;
        this.scoreboardPort = scoreboardPort;
        this.predictionScoringService = predictionScoringService;
    }

    public void execute(int round) {
//        try {
//            logInput(round);
//
//            var total = System.currentTimeMillis();
//
////            validateRoundIsNotClosed(round);
//
//            List<Match> matches = matchPort.findByRoundAndStatus(round, "CLOSED");
////            validateAllMatchesAreClosed(matches);
//            List<String> matchIds = matches.stream()
//                    .map(Match::getId)
//                    .toList();
//
//            List<Bet> allBets = betPort.findByMatchIdInAndPointsEarnedIsNull(matchIds);
//
//            if (allBets.isEmpty()) {
//                log.info("No bets found for round {}", round);
//                return;
//            }
//
//            Map<String, Match> matchesById = getMatchesWithScoreById(matches);
//
//            List<Bet> scoredBets = calculatePointsForBets(allBets, matchesById);
//
//            betPort.saveAll(scoredBets);
//
//            closeAllMatchesIfNeeded(matches);
//
//            saveScoreboardForRound(round, scoredBets);
//
//            log.info("Finished Score bets use case. Took {} s", (System.currentTimeMillis() - total) / 1000);
//        } catch (ScoreException e) {
//            logError(e);
//            throw e;
//        }
//    }
//
//    private void validateRoundIsNotClosed(int round) {
//        if (!scoreboardPort.findByRound(round).isEmpty()) {
//            throw new ScoreException("Round " + round + " has already been executed!");
//        }
//    }
//
//    private void validateAllMatchesAreClosed(List<Match> matches) {
//        boolean hasOpenMatch = matches.stream().anyMatch(m -> "OPEN".equals(m.getStatus()));
//        if (hasOpenMatch) {
//            throw new ScoreException("You can't settle the round with OPEN matches.");
//        }
//    }
//
//    private Map<String, Match> getMatchesWithScoreById(List<Match> matches) {
//        return matches.stream()
//                .filter(m -> m.getScore() != null)
//                .collect(Collectors.toMap(Match::getId, m -> m));
//    }
//
//    private List<Bet> calculatePointsForBets(List<Bet> allBets, Map<String, Match> matchesById) {
//        long start = System.currentTimeMillis();
//
//        log.info("Starting calculatePointsForBets");
//        Map<String, List<Bet>> betsGroupedByMatch = allBets.stream()
//                .collect(Collectors.groupingBy(Bet::getMatchId));
//
//        List<Bet> scoredBets = new ArrayList<>();
//
//        for (Map.Entry<String, List<Bet>> entry : betsGroupedByMatch.entrySet()) {
//            Match match = matchesById.get(entry.getKey());
//            if (match == null) continue;
//
//            List<Bet> bets = entry.getValue();
//            for (Bet bet : bets) {
//                List<Bet> otherBets = bets.stream()
//                        .filter(b -> !b.getId().equals(bet.getId()))
//                        .toList();
//
//                int points = predictionScoringService.calculate(match, bet, otherBets);
//                bet.setPointsEarned(points);
//                scoredBets.add(bet);
//            }
//        }
//
//        log.info("Finished calculatePointsForBets took {} s", (System.currentTimeMillis() - start) / 1000);
//        return scoredBets;
//    }
//
//    private void closeAllMatchesIfNeeded(List<Match> matches) {
//        for (Match match : matches) {
//            Date now = new Date();
//
//            if (match.getMatchDate().before(now) && !"CLOSED".equals(match.getStatus())) {
//                match.setStatus("CLOSED");
//                matchPort.save(match);
//            }
//        }
//    }
//
//    private void saveScoreboardForRound(int round, List<Bet> bets) {
//        Map<String, Integer> roundScores = computeUserScores(bets);
//
//        List<Scoreboard> roundScoreboard = toScoreboardList(round, roundScores);
//
//        List<String> usernames = bets.stream().map(Bet::getUsername).distinct().toList();
//
//        List<Scoreboard> roundAlreadyExists = scoreboardPort.findByRoundAndUsernames(round, usernames);
//        if (!roundAlreadyExists.isEmpty()) {
//            log.warn("Scoreboard for round {} already exists. Skipping save.", round);
//            return;
//        }
//
//        scoreboardPort.saveAll(roundScoreboard);
//
//        log.info("Saved score board for round {}", round);
//
//
//        updateTotalScoreboard(roundScores);
//
//
//    }
//
//    private Map<String, Integer> computeUserScores(List<Bet> bets) {
//        return bets.stream()
//                .collect(Collectors.groupingBy(
//                        Bet::getUsername,
//                        Collectors.summingInt(Bet::getPointsEarned)
//                ));
//    }
//
//    private List<Scoreboard> toScoreboardList(int round, Map<String, Integer> userScores) {
//        return userScores.entrySet().stream()
//                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
//                .map(entry -> new Scoreboard(null, round, entry.getKey(), entry.getValue()))
//                .collect(Collectors.toList());
//    }
//
//    private void updateTotalScoreboard(Map<String, Integer> roundScores) {
//        long start = System.currentTimeMillis();
//        log.info("starting updateTotalScoreboard");
//
//        List<String> usernames = new ArrayList<>(roundScores.keySet());
//        List<Scoreboard> existingTotals = scoreboardPort.findByRoundAndUsernames(0, usernames);
//
//        Map<String, Scoreboard> totalByUser = existingTotals.stream()
//                .collect(Collectors.toMap(Scoreboard::username, s -> s));
//
//        List<Scoreboard> updatedTotals = roundScores.entrySet().stream()
//                .map(entry -> {
//                    String username = entry.getKey();
//                    int roundPoints = entry.getValue();
//                    Scoreboard existing = totalByUser.get(username);
//
//                    return (existing == null)
//                            ? new Scoreboard(null, 0, username, roundPoints)
//                            : new Scoreboard(existing.id(), 0, username, existing.points() + roundPoints);
//                })
//                .toList();
//
//        scoreboardPort.saveAll(updatedTotals);
//        log.info("Finished updateTotalScoreboard took {} s", (System.currentTimeMillis() - start) / 1000);
    }
}
