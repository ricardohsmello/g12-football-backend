package br.com.g12.service;

import br.com.g12.model.Bet;
import br.com.g12.model.Score;
import br.com.g12.model.Scoreboard;
import br.com.g12.port.ScoreboardPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoundScoreboardServiceTest {

    @Mock
    private ScoreboardPort scoreboardPort;

    @InjectMocks
    private RoundScoreboardService service;

    private Bet bet(String username, int points) {
        return new Bet("id", "matchId", username, new Score(1, 1), 13, points, new Date());
    }

    @Test
    void shouldInsertNewRoundEntriesForUsersWithoutScore() {
        List<Bet> bets = List.of(bet("lucas", 10), bet("joao", 5));

        when(scoreboardPort.findByRoundAndUsernames(eq(13), anyList())).thenReturn(List.of());
        when(scoreboardPort.findByRoundAndUsernames(eq(0), anyList())).thenReturn(List.of());

        service.execute(bets, 13);

        verify(scoreboardPort, times(2)).saveAll(any());
    }

    @Test
    void shouldAccumulatePointsIfUserAlreadyHasScoreInRound() {
        List<Bet> bets = List.of(bet("lucas", 10));

        when(scoreboardPort.findByRoundAndUsernames(eq(13), anyList()))
                .thenReturn(List.of(new Scoreboard("id-lucas", 13, "lucas", 5)));

        when(scoreboardPort.findByRoundAndUsernames(eq(0), anyList()))
                .thenReturn(List.of(new Scoreboard("total-id", 0, "lucas", 15)));

        ArgumentCaptor<List<Scoreboard>> captor = ArgumentCaptor.forClass(List.class);

        service.execute(bets, 13);

        verify(scoreboardPort, times(2)).saveAll(captor.capture());

        List<List<Scoreboard>> allSaves = captor.getAllValues();
        List<Scoreboard> round = allSaves.get(0);
        List<Scoreboard> total = allSaves.get(1);

        Scoreboard roundEntry = round.getFirst();
        assertEquals(13, roundEntry.round());
        assertEquals("lucas", roundEntry.username());
        assertEquals(15, roundEntry.points());

        Scoreboard totalEntry = total.getFirst();
        assertEquals(0, totalEntry.round());
        assertEquals("lucas", totalEntry.username());
        assertEquals(25, totalEntry.points());
    }

    @Test
    void shouldInsertNewAndUpdateExistingUsersInSameRound() {
        List<Bet> bets = List.of(bet("lucas", 10), bet("ana", 8));

        when(scoreboardPort.findByRoundAndUsernames(eq(13), anyList()))
                .thenReturn(List.of(new Scoreboard("id-lucas", 13, "lucas", 5)));

        when(scoreboardPort.findByRoundAndUsernames(eq(0), anyList()))
                .thenReturn(List.of());

        ArgumentCaptor<List<Scoreboard>> captor = ArgumentCaptor.forClass(List.class);

        service.execute(bets, 13);

        verify(scoreboardPort, times(2)).saveAll(captor.capture());

        List<Scoreboard> round = captor.getAllValues().getFirst();
        assertEquals(2, round.size());

        Scoreboard lucas = round.stream().filter(s -> s.username().equals("lucas")).findFirst().orElseThrow();
        Scoreboard ana = round.stream().filter(s -> s.username().equals("ana")).findFirst().orElseThrow();

        assertEquals(15, lucas.points());
        assertEquals(8, ana.points());
    }

    @Test
    void shouldDoNothingIfNoBetsProvided() {
        service.execute(List.of(), 13);
        verifyNoInteractions(scoreboardPort);
    }
}
