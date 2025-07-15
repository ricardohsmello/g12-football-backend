package br.com.g12.service;

import br.com.g12.model.Bet;
import br.com.g12.model.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoundScoreboardService {


//    public void calculate() {
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
//    private void updateTotalScoreboard(Map<String, Integer> roundScores) {
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
//    }

}
