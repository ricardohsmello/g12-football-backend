package br.com.g12.port;

import br.com.g12.model.Scoreboard;

import java.util.List;

public interface ScoreboardPort {
    List<Scoreboard> findByRound(int round);
    void saveAll(List<Scoreboard> scoreboards);
    Scoreboard findByRoundAndUsername(int round, String username);
    void save(Scoreboard scoreboard);
}