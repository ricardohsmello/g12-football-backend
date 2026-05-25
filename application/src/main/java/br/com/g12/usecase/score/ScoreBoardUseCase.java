package br.com.g12.usecase.score;

import br.com.g12.model.CompetitionDefaults;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.ScoreboardPort;

import java.util.List;

public class ScoreBoardUseCase {

    private final ScoreboardPort scoreboardPort;

    public ScoreBoardUseCase(ScoreboardPort scoreboardPort) {
        this.scoreboardPort = scoreboardPort;
    }

    public List<Scoreboard> execute(int round, int year) {
        return execute(CompetitionDefaults.DEFAULT_COMPETITION_ID, round, year);
    }

    public List<Scoreboard> execute(String competitionId, int round, int year) {
        return scoreboardPort.findByCompetitionIdAndRoundAndYear(CompetitionDefaults.competitionIdOrDefault(competitionId), round, year);
    }
}
