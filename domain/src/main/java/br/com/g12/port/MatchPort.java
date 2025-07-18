package br.com.g12.port;

import br.com.g12.model.Match;
import br.com.g12.model.MatchWithPrediction;

import java.util.Date;
import java.util.List;

public interface MatchPort {
    Match save(Match match);
    Match find(String id);
    List<Match> findByRoundAndStatus(int round, String status);
    List<MatchWithPrediction> findByRoundUser(String username, int round);
    int closeExpiredMatches(Date date);
    int findNextMatchRound();
}
