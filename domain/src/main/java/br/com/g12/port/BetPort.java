package br.com.g12.port;

import br.com.g12.model.Bet;

import java.util.List;

public interface BetPort {

    Bet save(Bet bet);
    void saveAll(List<Bet> bets);
    List<Bet> findByMatchIdInAndPointsEarnedIsNull(List<String> matchIds);
    Bet findById(String id);
    List<Bet> findByMatchId(String id);
    int countDistinctUsernamesByRound(int round);
}
