package br.com.g12.usecase.score;

import br.com.g12.model.Scoreboard;
import br.com.g12.port.MatchPort;
import br.com.g12.port.ScoreboardPort;
import br.com.g12.usecase.match.CloseExpiredMatchesUseCase;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ScoreBoardUseCaseTest {


    private final ScoreboardPort scoreboardPort = mock(ScoreboardPort.class);
    private final ScoreBoardUseCase useCase = new ScoreBoardUseCase(scoreboardPort);


    public List<Scoreboard> execute(int round) {
        return scoreboardPort.findByRound(round);
    }

    @Test
    public void should_execute_schedule_successfully() {

        int round = 10;
        List<Scoreboard> mockScoreboards = List.of(
                new Scoreboard("1",13,"alice",  30),
                new Scoreboard("2",13,"ricardo",  23)
        );

        when(scoreboardPort.findByRound(round)).thenReturn(mockScoreboards);

        List<Scoreboard> result = useCase.execute(round);

        assertEquals(2, result.size());
        assertEquals("alice", result.getFirst().username());
        assertEquals(30, result.getFirst().points());
        verify(scoreboardPort).findByRound(round);

    }
}
