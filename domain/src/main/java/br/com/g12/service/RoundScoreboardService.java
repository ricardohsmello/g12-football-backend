package br.com.g12.service;

import br.com.g12.model.Bet;
import br.com.g12.model.CompetitionDefaults;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.ScoreboardPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class RoundScoreboardService {

    private static final Logger log = LoggerFactory.getLogger(RoundScoreboardService.class);
    private final ScoreboardPort scoreboardPort;

    public RoundScoreboardService(ScoreboardPort scoreboardPort) {
        this.scoreboardPort = scoreboardPort;
    }

    public void execute(List<Bet> bets, int round, int year) {
        execute(bets, CompetitionDefaults.DEFAULT_COMPETITION_ID, round, year);
    }

    public void execute(List<Bet> bets, String competitionId, int round, int year) {
        if (bets == null || bets.isEmpty()) {
            log.warn("No bets provided for round {}. Skipping.", round);
            return;
        }

        Map<String, Integer> userPoints = computeUserPoints(bets);

        savePerRoundScoreboard(competitionId, round, year, userPoints);
        updateTotalScoreboard(competitionId, year, userPoints);

        log.info("Scoreboard updated for competition {}, round {} and year {}", competitionId, round, year);
    }

    private Map<String, Integer> computeUserPoints(List<Bet> bets) {
        return bets.stream()
                .collect(Collectors.groupingBy(
                        Bet::getUsername,
                        Collectors.summingInt(Bet::getPointsEarned)
                ));
    }

    private void savePerRoundScoreboard(String competitionId, int round, int year, Map<String, Integer> userPoints) {
        List<String> usernames = new ArrayList<>(userPoints.keySet());
        List<Scoreboard> existing = scoreboardPort.findByCompetitionIdAndRoundAndYearAndUsernames(competitionId, round, year, usernames);

        Map<String, Scoreboard> existingMap = existing.stream()
                .collect(Collectors.toMap(Scoreboard::username, s -> s));

        List<Scoreboard> updatedScoreboards = userPoints.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    int newPoints = entry.getValue();
                    Scoreboard existingScore = existingMap.get(username);

                    return (existingScore == null)
                            ? new Scoreboard(null, competitionId, round, username, newPoints, year)
                            : new Scoreboard(existingScore.id(), competitionId, round, username, existingScore.points() + newPoints, year);
                })
                .sorted(Comparator.comparingInt(Scoreboard::points).reversed())
                .toList();

        scoreboardPort.saveAll(updatedScoreboards);
        log.info("Saved/Updated {} scoreboard entries for round {} and year {}", updatedScoreboards.size(), round, year);
    }

    private void updateTotalScoreboard(String competitionId, int year, Map<String, Integer> roundScores) {
        List<String> usernames = new ArrayList<>(roundScores.keySet());
        List<Scoreboard> existingTotals = scoreboardPort.findByCompetitionIdAndRoundAndYearAndUsernames(competitionId, 0, year, usernames);

        Map<String, Scoreboard> totalByUser = existingTotals.stream()
                .collect(Collectors.toMap(Scoreboard::username, s -> s));

        List<Scoreboard> updatedTotals = roundScores.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    int newPoints = entry.getValue();
                    Scoreboard existing = totalByUser.get(username);

                    return (existing == null)
                            ? new Scoreboard(null, competitionId, 0, username, newPoints, year)
                            : new Scoreboard(existing.id(), competitionId, 0, username, existing.points() + newPoints, year);
                })
                .toList();

        scoreboardPort.saveAll(updatedTotals);
        log.info("Updated total scoreboard for {} users", updatedTotals.size());
    }
}
