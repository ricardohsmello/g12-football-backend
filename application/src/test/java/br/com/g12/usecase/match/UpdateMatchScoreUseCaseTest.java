package br.com.g12.usecase.match;

import br.com.g12.model.Match;
import br.com.g12.model.Score;
import br.com.g12.port.MatchPort;
import br.com.g12.request.ScoreRequest;
import br.com.g12.validators.ScoreValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UpdateMatchScoreUseCaseTest {

    private MatchPort matchPort;
    private ScoreValidator scoreValidator;
    private UpdateMatchScoreUseCase useCase;

    @BeforeEach
    void setup() {
        matchPort = mock(MatchPort.class);
        scoreValidator = mock(ScoreValidator.class);
        useCase = new UpdateMatchScoreUseCase(matchPort,  scoreValidator);
    }

    @Test
    public void should_create_match_successfully() {
        String matchId = "match-123";
        ScoreRequest request = new ScoreRequest(2, 1);
        Score score = new Score(2, 1);

        Match match = new Match(matchId, 1, "Real Madrid", "Corinthians", new Date(), null, "Open");

        when(matchPort.find(any(String.class))).thenReturn(match);
        useCase.execute(matchId, request);

        verify(matchPort).find(matchId);
        verify(scoreValidator).validate(score);
        verify(matchPort).save(match);

        assert match.getScore().equals(score);
    }
}