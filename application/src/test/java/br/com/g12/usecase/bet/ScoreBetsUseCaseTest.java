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
import br.com.g12.service.RoundScoreboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ScoreBetsUseCaseTest {

    private MatchPort matchPort;
    private BetPort betPort;
    private PredictionScoringService predictionScoringService;
    private RoundScoreboardService roundScoreboardService;

    private ScoreBetsUseCase useCase;

    @BeforeEach
    void setUp() {
        matchPort = mock(MatchPort.class);
        betPort = mock(BetPort.class);
        predictionScoringService = mock(PredictionScoringService.class);
        roundScoreboardService = mock(RoundScoreboardService.class);

        useCase = new ScoreBetsUseCase(
                matchPort,
                betPort,
                predictionScoringService,
                roundScoreboardService
        );
    }

    @Test
    void should_score_points_correctly_and_delegate_to_scoreboard_service() {
        Match match = MatchFake.builder()
                .status("CLOSED")
                .id("match-1")
                .homeTeam("Corinthians")
                .awayTeam("Bragantino")
                .round(13)
                .score(new Score(1, 2))
                .build();

        Bet bet1 = new Bet("1", "match-1", "ricas", new Score(2, 1), 13, null, new Date());
        Bet bet2 = new Bet("2", "match-1", "henrique", new Score(1, 2), 13, null, new Date());

        List<Bet> bets = List.of(bet1, bet2);
        List<Match> matches = List.of(match);

        when(matchPort.findByRoundAndStatus(13, "CLOSED")).thenReturn(matches);
        when(betPort.findByMatchIdInAndPointsEarnedIsNull(List.of("match-1"))).thenReturn(bets);

        when(predictionScoringService.calculate(eq(match), eq(bet1), anyList())).thenReturn(0);
        when(predictionScoringService.calculate(eq(match), eq(bet2), anyList())).thenReturn(11);

        useCase.execute(13);

        verify(betPort).saveAll(anyList());
        verify(predictionScoringService, times(2)).calculate(any(), any(), anyList());

        ArgumentCaptor<List<Bet>> betCaptor = ArgumentCaptor.forClass(List.class);
        verify(roundScoreboardService).execute(betCaptor.capture(), eq(13));

        List<Bet> scored = betCaptor.getValue();
        assertEquals(2, scored.size());

        assertTrue(scored.stream().anyMatch(b -> b.getUsername().equals("ricas") && b.getPointsEarned() == 0));
        assertTrue(scored.stream().anyMatch(b -> b.getUsername().equals("henrique") && b.getPointsEarned() == 11));
    }

    @Test
    public void should_not_save_score_when_bets_is_empty() {
        Match match = MatchFake.builder()
                .status("CLOSED")
                .id("match-1")
                .homeTeam("Corinthians")
                .awayTeam("Bragantino")
                .round(13)
                .score(new Score(1, 2))
                .build();

        List<Match> matches = List.of(match);

        when(matchPort.findByRoundAndStatus(13, "CLOSED")).thenReturn(matches);
        when(betPort.findByMatchIdInAndPointsEarnedIsNull(List.of("match-1"))).thenReturn(new ArrayList<>());

        useCase.execute(13);

        verify(betPort, never()).saveAll(anyList());
        verify(roundScoreboardService, never()).execute(anyList(), anyInt());

    }


}
