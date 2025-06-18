package br.com.g12.service;

import br.com.g12.fake.BetFake;
import br.com.g12.fake.MatchFake;
import br.com.g12.model.Bet;
import br.com.g12.model.Score;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoringServiceTest {

    @Test
    public void should_return_0_points_when_prediction_is_incorrect() {
        ScoringService scoringService = new ScoringService();

        var userPrediction = new Score(2, 1);
        var matchResult = new Score(1, 1);

        var bet = new BetFake.BetBuilder()
                .prediction(userPrediction)
                .build();

        var match = new MatchFake.MatchBuilder()
                .homeTeam("Corinthians")
                .awayTeam("Santos")
                .score(matchResult)
                .build();

        List<Bet> otherBets = new BetFake.BetListBuilder("match1")
                .add("Maria", new Score(1, 0)).build();

        int pointsEarned = scoringService.calculate(match, bet, otherBets);

        assertEquals(pointsEarned, 0);
    }

    @Test
    public void should_return_5_points_when_winner_is_correct() {
        ScoringService scoringService = new ScoringService();

        var userPrediction = new Score(2, 1);
        var matchResult = new Score(1, 0);

       var bet = new BetFake.BetBuilder()
                .prediction(userPrediction)
                .build();

       var match = new MatchFake.MatchBuilder()
               .homeTeam("Corinthians")
               .awayTeam("Santos")
               .score(matchResult)
               .build();

        List<Bet> otherBets = new BetFake.BetListBuilder("match1")
                .add("Maria", new Score(1, 0)).build();

        int pointsEarned = scoringService.calculate(match, bet, otherBets);

        assertEquals(pointsEarned, 5);
    }

    @Test
    @DisplayName("Should return only 10 points for exact score when more than one player guessed it correctly (no uniqueness bonus applied)")
    public void should_return_10_points_when_score_is_exact() {
        ScoringService scoringService = new ScoringService();

        var userPrediction = new Score(3, 0);
        var matchResult = new Score(3, 0);

        var bet = new BetFake.BetBuilder()
                .prediction(userPrediction)
                .build();

        var match = new MatchFake.MatchBuilder()
                .homeTeam("Corinthians")
                .awayTeam("Santos")
                .score(matchResult)
                .build();

        List<Bet> otherBets = new BetFake.BetListBuilder("match1")
                .add("Maria", new Score(3, 0)).build();

        int pointsEarned = scoringService.calculate(match, bet, otherBets);

        assertEquals(pointsEarned, 10);
    }

    @Test
    @DisplayName("Should add 1 bonus point when only one player gets the exact score correctly (10 + 1 points for being the only one)")
    public void should_return_11_points_when_only_user_with_exact_score() {
        ScoringService scoringService = new ScoringService();

        var userPrediction = new Score(3, 0);
        var matchResult = new Score(3, 0);

        var bet = new BetFake.BetBuilder()
                .prediction(userPrediction)
                .build();

        var match = new MatchFake.MatchBuilder()
                .id("match1")
                .homeTeam("Corinthians")
                .awayTeam("Santos")
                .score(matchResult)
                .build();

        List<Bet> otherBets = new BetFake.BetListBuilder("match1")
                .add("Daniel", new Score(1, 0))
                .add("Antonio", new Score(2, 0))
                .add("Braulio", new Score(1, 2))
                .build();

        int pointsEarned = scoringService.calculate(match, bet, otherBets);

        assertEquals(pointsEarned, 11);
    }

    @Test
    @DisplayName("Should return 12 points: 10 for exact score, 1 for being the only one to guess it, and 1 for a match with 4+ total goals")
    public void should_return_12_points_when_score_is_exact() {
        ScoringService scoringService = new ScoringService();

        var userPrediction = new Score(4, 2);
        var matchResult = new Score(4, 2);

        var bet = new BetFake.BetBuilder()
                .prediction(userPrediction)
                .build();

        var match = new MatchFake.MatchBuilder()
                .homeTeam("Corinthians")
                .awayTeam("Santos")
                .score(matchResult)
                .build();

        List<Bet> otherBets = new BetFake.BetListBuilder("match1")
                .add("Maria", new Score(2, 0))
                .add("Pedro", new Score(3, 1))
                .add("Joao", new Score(0, 1))
                .add("Julia", new Score(2, 4))
                .build();

        int pointsEarned = scoringService.calculate(match, bet, otherBets);
        assertEquals(pointsEarned, 12);
    }

}
