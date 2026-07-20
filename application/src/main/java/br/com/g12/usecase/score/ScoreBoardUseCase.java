package br.com.g12.usecase.score;

import br.com.g12.model.CompetitionDefaults;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.ScoreboardPort;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreBoardUseCase {

    private static final int EXTRA_POINTS_ROUND = 10;

    private final ScoreboardPort scoreboardPort;

    public ScoreBoardUseCase(ScoreboardPort scoreboardPort) {
        this.scoreboardPort = scoreboardPort;
    }

    public List<Scoreboard> execute(int round, int year) {
        return execute(CompetitionDefaults.DEFAULT_COMPETITION_ID, round, year);
    }

    public List<Scoreboard> execute(String competitionId, int round, int year) {
        String normalizedCompetitionId = CompetitionDefaults.competitionIdOrDefault(competitionId);
        List<Scoreboard> scoreboards = scoreboardPort.findByCompetitionIdAndRoundAndYear(normalizedCompetitionId, round, year);

        if (round == EXTRA_POINTS_ROUND) {
            return scoreboards;
        }

        Map<String, List<String>> predictionByUsername = scoreboardPort
                .findByCompetitionIdAndRoundAndYear(normalizedCompetitionId, EXTRA_POINTS_ROUND, year).stream()
                .filter(s -> !s.prediction().isEmpty())
                .collect(Collectors.toMap(Scoreboard::username, Scoreboard::prediction, (first, ignored) -> first));

        return scoreboards.stream()
                .map(s -> s.withPrediction(predictionByUsername.getOrDefault(s.username(), List.of())))
                .toList();
    }
}
