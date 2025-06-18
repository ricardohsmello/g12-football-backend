package br.com.g12.usecase.score;

import br.com.g12.model.Scoreboard;
import br.com.g12.port.ScoreboardPort;

import java.util.List;

public class ScoreBoardUseCase {

    private final ScoreboardPort scoreboardPort;

    public ScoreBoardUseCase(ScoreboardPort scoreboardPort) {
        this.scoreboardPort = scoreboardPort;
    }

    public List<Scoreboard> execute(int round) {
        return scoreboardPort.findByRound(round);
    }
}
