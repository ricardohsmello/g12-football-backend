package br.com.g12.port;

import br.com.g12.model.Scoreboard;
import br.com.g12.model.CompetitionDefaults;

import java.util.List;

public interface ScoreboardPort {
    List<Scoreboard> findByCompetitionIdAndRoundAndYear(String competitionId, int round, int year);
    void saveAll(List<Scoreboard> scoreboards);
    Scoreboard findByCompetitionIdAndRoundAndYearAndUsername(String competitionId, int round, int year, String username);
    List<Scoreboard> findByCompetitionIdAndRoundAndYearAndUsernames(String competitionId, int round, int year, List<String> usernames);

    void save(Scoreboard scoreboard);

    default List<Scoreboard> findByRoundAndYear(int round, int year) {
        return findByCompetitionIdAndRoundAndYear(CompetitionDefaults.DEFAULT_COMPETITION_ID, round, year);
    }

    default Scoreboard findByRoundAndYearAndUsername(int round, int year, String username) {
        return findByCompetitionIdAndRoundAndYearAndUsername(CompetitionDefaults.DEFAULT_COMPETITION_ID, round, year, username);
    }

    default List<Scoreboard> findByRoundAndYearAndUsernames(int round, int year, List<String> usernames) {
        return findByCompetitionIdAndRoundAndYearAndUsernames(CompetitionDefaults.DEFAULT_COMPETITION_ID, round, year, usernames);
    }
}
