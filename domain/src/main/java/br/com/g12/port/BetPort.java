package br.com.g12.port;

import br.com.g12.model.Bet;
import br.com.g12.model.CompetitionDefaults;

import java.util.List;

public interface BetPort {

    Bet save(Bet bet);
    void saveAll(List<Bet> bets);
    List<Bet> findByMatchIdInAndPointsEarnedIsNull(List<String> matchIds);
    List<Bet> findByMatchIdIn(List<String> matchIds);
    int countDistinctUsernamesByCompetitionIdAndRound(String competitionId, int round);

    default int countDistinctUsernamesByRound(int round) {
        return countDistinctUsernamesByCompetitionIdAndRound(CompetitionDefaults.DEFAULT_COMPETITION_ID, round);
    }
}
