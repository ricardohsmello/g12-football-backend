package br.com.g12.usecase.bet;

import br.com.g12.fake.BetFake;
import br.com.g12.fake.MatchFake;
import br.com.g12.model.Bet;
import br.com.g12.model.Match;
import br.com.g12.model.Score;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.BetPort;
import br.com.g12.port.MatchPort;
import br.com.g12.port.ScoreboardPort;
import br.com.g12.service.ScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ScoreBetsUseCaseTest {

    private MatchPort matchPort;
    private BetPort betPort;
    private ScoreboardPort scoreboardPort;
    private ScoringService scoringService;
    private ScoreBetsUseCase useCase;


    @BeforeEach
    void setUp() {
        matchPort = mock(MatchPort.class);
        betPort = mock(BetPort.class);
        scoreboardPort = mock(ScoreboardPort.class);
        scoringService = mock(ScoringService.class);
        useCase = new ScoreBetsUseCase(matchPort, betPort, scoreboardPort, scoringService);
    }


    @Test
    public void should_score_points_correctly() {
        Match match = MatchFake.builder().status("CLOSED").score(new br.com.g12.model.Score(2, 1)).build();
        Bet bet = BetFake.builder()
                .setId("1")
                .setUserId("ricas")

                .build();

        Bet bet2 = BetFake.builder()
                .setId("2")
                .setUserId("henrique")

                .build();

        List<Match> matches = List.of(match);
        List<Bet> bets = List.of(bet, bet2);

        when(matchPort.findByRound(1)).thenReturn(matches);
        when(betPort.findByMatchId(match.getId())).thenReturn(bets);
        when(scoringService.calculate(eq(match), eq(bet), anyList())).thenReturn(10);

        useCase.execute(1);

        verify(betPort).save(bet);
        verify(scoringService, times(2)).calculate(any(), any(), anyList());
    }

    @Test
    public void should_score_points_correctly2() {
        Match match = MatchFake
                    .builder()
                    .status("CLOSED")
                    .score(new Score(2, 1))
                .build();

        Bet bet1 = BetFake.builder()
                .setId("1")
                .setUserId("ricas")
                .build();

        Bet bet2 = BetFake.builder()
                .setId("2")
                .setUserId("henrique")
                .build();

        List<Match> matches = List.of(match);
        List<Bet> bets = List.of(bet1, bet2);

        when(matchPort.findByRound(1)).thenReturn(matches);
        when(betPort.findByMatchId(match.getId())).thenReturn(bets);
        when(scoringService.calculate(eq(match), eq(bet1), anyList())).thenReturn(10);
        when(scoringService.calculate(eq(match), eq(bet2), anyList())).thenReturn(5);
        when(scoreboardPort.findByRoundAndUsername(0, "ricas")).thenReturn(null);
        when(scoreboardPort.findByRoundAndUsername(0, "henrique")).thenReturn(null);

        useCase.execute(1);

        verify(betPort).save(bet1);
        verify(betPort).save(bet2);

        verify(matchPort, atLeastOnce()).save(argThat(m ->
                m.getId().equals(match.getId()) && "CLOSED".equals(m.getStatus())));

        ArgumentCaptor<List<Scoreboard>> scoreboardCaptor = ArgumentCaptor.forClass(List.class);
        verify(scoreboardPort).saveAll(scoreboardCaptor.capture());

        List<Scoreboard> roundScoreboard = scoreboardCaptor.getValue();
        assertEquals(2, roundScoreboard.size());

        verify(scoreboardPort).findByRoundAndUsername(0, "ricas");
        verify(scoreboardPort).findByRoundAndUsername(0, "henrique");

        verify(scoreboardPort).save(argThat(s ->
                s.round() == 0 && s.username().equals("ricas") && s.points() == 10));

        verify(scoreboardPort).save(argThat(s ->
                s.round() == 0 && s.username().equals("henrique") && s.points() == 5));
    }


}
