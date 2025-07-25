package br.com.g12.service;

import br.com.g12.model.Bet;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.ScoreboardPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoundScoreboardService {

    private static final Logger log = LoggerFactory.getLogger(RoundScoreboardService.class);
    private final ScoreboardPort scoreboardPort;

    public RoundScoreboardService(ScoreboardPort scoreboardPort) {
        this.scoreboardPort = scoreboardPort;
    }

    public void execute(List<Bet> bets, int round) {
        if (bets == null || bets.isEmpty()) {
            log.warn("No bets provided for round {}. Skipping.", round);
            return;
        }

        Map<String, Integer> userPoints = computeUserPoints(bets);

        savePerRoundScoreboard(round, userPoints);
        updateTotalScoreboard(userPoints);

        log.info("Scoreboard updated for round {}", round);
    }

    private Map<String, Integer> computeUserPoints(List<Bet> bets) {
        return bets.stream()
                .collect(Collectors.groupingBy(
                        Bet::getUsername,
                        Collectors.summingInt(Bet::getPointsEarned)
                ));
    }

    private void savePerRoundScoreboard(int round, Map<String, Integer> userPoints) {
        List<String> usernames = new ArrayList<>(userPoints.keySet());
        List<Scoreboard> existing = scoreboardPort.findByRoundAndUsernames(round, usernames);

        Map<String, Scoreboard> existingMap = existing.stream()
                .collect(Collectors.toMap(Scoreboard::username, s -> s));

        List<Scoreboard> updatedScoreboards = userPoints.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    int newPoints = entry.getValue();
                    Scoreboard existingScore = existingMap.get(username);

                    return (existingScore == null)
                            ? new Scoreboard(null, round, username, newPoints)
                            : new Scoreboard(existingScore.id(), round, username, existingScore.points() + newPoints);
                })
                .sorted(Comparator.comparingInt(Scoreboard::points).reversed())
                .toList();

        scoreboardPort.saveAll(updatedScoreboards);
        log.info("Saved/Updated {} scoreboard entries for round {}", updatedScoreboards.size(), round);
    }

    private void updateTotalScoreboard(Map<String, Integer> roundScores) {
        List<String> usernames = new ArrayList<>(roundScores.keySet());
        List<Scoreboard> existingTotals = scoreboardPort.findByRoundAndUsernames(0, usernames);

        Map<String, Scoreboard> totalByUser = existingTotals.stream()
                .collect(Collectors.toMap(Scoreboard::username, s -> s));

        List<Scoreboard> updatedTotals = roundScores.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    int newPoints = entry.getValue();
                    Scoreboard existing = totalByUser.get(username);

                    return (existing == null)
                            ? new Scoreboard(null, 0, username, newPoints)
                            : new Scoreboard(existing.id(), 0, username, existing.points() + newPoints);
                })
                .toList();

        scoreboardPort.saveAll(updatedTotals);
        log.info("Updated total scoreboard for {} users", updatedTotals.size());
    }
}
