package br.com.g12.port;

import br.com.g12.model.Scoreboard;

import java.util.List;

public interface ScoreboardPort {
    List<Scoreboard> findByRoundAndYear(int round, int year);
    void saveAll(List<Scoreboard> scoreboards);
    Scoreboard findByRoundAndYearAndUsername(int round, int year, String username);
    List<Scoreboard> findByRoundAndYearAndUsernames(int round, int year, List<String> usernames);

    void save(Scoreboard scoreboard);
}
