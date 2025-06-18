package br.com.g12.service;

import br.com.g12.model.Bet;
import br.com.g12.model.Match;

import java.util.List;

public class ScoringService {

    public int calculate(Match match, Bet bet, List<Bet> allBetsForMatch) {
        int actualHome = match.getScore().homeTeam();
        int actualAway = match.getScore().awayTeam();

        int predictedHome = bet.getPrediction().homeTeam();
        int predictedAway = bet.getPrediction().awayTeam();

        boolean exact = actualHome == predictedHome && actualAway == predictedAway;

        if (exact) {
            boolean has4Goals = (actualHome + actualAway) >= 4;
            boolean isUniqueExactResult = allBetsForMatch.stream().noneMatch(b -> b.getPrediction().homeTeam() == actualHome
                    && b.getPrediction().awayTeam() == actualAway);

            return 10 + (has4Goals ? 1 : 0) + (isUniqueExactResult ? 1 : 0);
        }

        boolean sameResult = resultType(actualHome, actualAway).equals(resultType(predictedHome, predictedAway));

        return sameResult ? 5 : 0;
    }

    private String resultType(int home, int away) {
        if (home > away) return "HOME_WIN";
        if (home < away) return "AWAY_WIN";
        return "DRAW";
    }
}
