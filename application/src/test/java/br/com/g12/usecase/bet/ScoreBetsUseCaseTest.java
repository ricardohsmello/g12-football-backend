package br.com.g12.usecase.bet;

import br.com.g12.exception.ScoreException;
import br.com.g12.fake.BetFake;
import br.com.g12.fake.MatchFake;
import br.com.g12.fake.ScoreBoardFake;
import br.com.g12.model.Bet;
import br.com.g12.model.Match;
import br.com.g12.model.Score;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.BetPort;
import br.com.g12.port.MatchPort;
import br.com.g12.port.ScoreboardPort;
import br.com.g12.service.PredictionScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ScoreBetsUseCaseTest {

    private MatchPort matchPort;
    private BetPort betPort;
    private ScoreboardPort scoreboardPort;
    private PredictionScoringService predictionScoringService;
    private ScoreBetsUseCase useCase;


    @BeforeEach
    void setUp() {
        matchPort = mock(MatchPort.class);
        betPort = mock(BetPort.class);
        scoreboardPort = mock(ScoreboardPort.class);
        predictionScoringService = mock(PredictionScoringService.class);
        useCase = new ScoreBetsUseCase(matchPort, betPort, scoreboardPort, predictionScoringService);
    }

    @Test
    public void should_throw_error_when_round_has_already_executed() {
        var round = 13;
        when(scoreboardPort.findByRound(round)).thenReturn(List.of(ScoreBoardFake.getOne()));
        ScoreException ex = assertThrows(ScoreException.class, () -> useCase.execute(13));
        assertEquals("Round " + round + " has already been executed!", ex.getMessage());
    }


    @Test
    public void should_throw_error_when_round_has_opened_matches() {
        var round = 13;
        when(scoreboardPort.findByRound(round)).thenReturn(new ArrayList<>());
        when(matchPort.findByRoundAndStatus(round, "CLOSED")).thenReturn(List.of(MatchFake.builder().status("OPEN").build()));

        ScoreException ex = assertThrows(ScoreException.class, () -> useCase.execute(13));
        assertEquals("You can't settle the round with OPEN matches.", ex.getMessage());
    }

    @Test
    public void should_score_points_correctly2() {

        Match match = MatchFake.builder()
                .status("CLOSED")
                .score(new Score(2, 1))
                .build();

        Bet bet1 = BetFake.builder()
                .setId("1")
                .setUserId("ricas")
                .setMatchId(match.getId())
                .setRound(1)
                .build();

        Bet bet2 = BetFake.builder()
                .setId("2")
                .setUserId("henrique")
                .setMatchId(match.getId())
                .setRound(1)
                .build();

        List<Bet> bets = List.of(bet1, bet2);
        List<Match> matches = List.of(match);

        when(matchPort.findByRoundAndStatus(1, "CLOSED")).thenReturn(matches);
        when(betPort.findByRoundAndPointsEarnedIsNull(1)).thenReturn(bets);
        when(scoreboardPort.findByRound(1)).thenReturn(List.of());
        when(scoreboardPort.findByRoundAndUsernames(eq(0), anyList())).thenReturn(List.of());
        when(predictionScoringService.calculate(eq(match), eq(bet1), anyList())).thenReturn(10);
        when(predictionScoringService.calculate(eq(match), eq(bet2), anyList())).thenReturn(5);

        useCase.execute(1);

        verify(betPort).saveAll(bets);
        verify(predictionScoringService, times(2)).calculate(any(), any(), anyList());

        ArgumentCaptor<List<Scoreboard>> scoreboardCaptor = ArgumentCaptor.forClass(List.class);
        verify(scoreboardPort, times(2)).saveAll(scoreboardCaptor.capture());

        List<List<Scoreboard>> allSaves = scoreboardCaptor.getAllValues();
        assertEquals(2, allSaves.size());

        List<Scoreboard> roundScoreboard = allSaves.get(0);
        List<Scoreboard> totalScoreboard = allSaves.get(1);

        assertEquals(2, roundScoreboard.size());
        assertTrue(roundScoreboard.stream().anyMatch(s -> s.round() == 1 && s.username().equals("ricas") && s.points() == 10));
        assertTrue(roundScoreboard.stream().anyMatch(s -> s.round() == 1 && s.username().equals("henrique") && s.points() == 5));

        assertEquals(2, totalScoreboard.size());
        assertTrue(totalScoreboard.stream().anyMatch(s -> s.round() == 0 && s.username().equals("ricas") && s.points() == 10));
        assertTrue(totalScoreboard.stream().anyMatch(s -> s.round() == 0 && s.username().equals("henrique") && s.points() == 5));
    }

    @Test
    public void test() {

        Match match = MatchFake.builder()
                .status("CLOSED")
                .id("1")
                .homeTeam("Corinthians")
                .awayTeam("Bragantino")
                .score(new Score(1, 2))
                .build();

        Match match2 = MatchFake.builder()
                .status("OPEN - (ADIADO)")
                .homeTeam("Santos")
                .awayTeam("Palmeiras")
                .build();

        var matches = List.of(match, match2);

        Bet bet1 = BetFake.builder()
                .setId("1")
                .setUserId("ricas")
                .setMatchId(match.getId())
                .setPrediction(new Score(2, 1))
                .setRound(13)
                .build();

        Bet bet2 = BetFake.builder()
                .setId("2")
                .setUserId("murilo")
                .setMatchId(match.getId())
                .setPrediction(new Score(1, 2))
                .setRound(13)
                .build();

        var bets = List.of(bet1, bet2);

        when(matchPort.findByRoundAndStatus(1, "CLOSED")).thenReturn(matches);
        when(betPort.findByRoundAndPointsEarnedIsNull(1)).thenReturn(bets);


        useCase.execute(1);

    }


}
