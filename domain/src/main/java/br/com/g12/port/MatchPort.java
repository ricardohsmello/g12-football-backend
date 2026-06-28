package br.com.g12.port;

import br.com.g12.model.Match;
import br.com.g12.model.MatchWithPrediction;
import br.com.g12.model.CompetitionDefaults;

import java.util.Date;
import java.util.List;

public interface MatchPort {
    Match save(Match match);
    Match find(String id);
    List<Match> findByCompetitionIdAndRoundAndStatus(String competitionId, int round, String status);
    List<Match> findByCompetitionIdAndRoundAndStatusAndMatchDateBetween(String competitionId, int round, String status, Date startDate, Date endDate);
    List<MatchWithPrediction> findByCompetitionIdAndRoundUserAndYear(String competitionId, String username, int round, int year);
    List<Match> findExpiredOpenMatches(Date now);
    int closeExpiredMatches(Date date);
    int findNextOpenRound(String competitionId);

    default List<Match> findByRoundAndStatus(int round, String status) {
        return findByCompetitionIdAndRoundAndStatus(CompetitionDefaults.DEFAULT_COMPETITION_ID, round, status);
    }

    default List<Match> findByRoundAndStatusAndMatchDateBetween(int round, String status, Date startDate, Date endDate) {
        return findByCompetitionIdAndRoundAndStatusAndMatchDateBetween(CompetitionDefaults.DEFAULT_COMPETITION_ID, round, status, startDate, endDate);
    }

    default List<MatchWithPrediction> findByRoundUserAndYear(String username, int round, int year) {
        return findByCompetitionIdAndRoundUserAndYear(CompetitionDefaults.DEFAULT_COMPETITION_ID, username, round, year);
    }

    default int findNextOpenRound() {
        return findNextOpenRound(CompetitionDefaults.DEFAULT_COMPETITION_ID);
    }
}
