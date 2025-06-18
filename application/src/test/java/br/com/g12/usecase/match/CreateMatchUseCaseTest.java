package br.com.g12.usecase.match;

import br.com.g12.model.Match;
import br.com.g12.port.MatchPort;
import br.com.g12.request.MatchRequest;
import br.com.g12.response.MatchResponse;
import br.com.g12.validators.MatchValidator;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateMatchUseCaseTest {

    private final MatchPort matchPort = mock(MatchPort.class);
    private final MatchValidator matchValidator = mock(MatchValidator.class);
    private final CreateMatchUseCase useCase = new CreateMatchUseCase(matchPort, matchValidator);

    @Test
    public void should_create_match_successfully() {
        MatchRequest request = new MatchRequest(1, "Real Madrid", "Corinthians", new Date(), "Open");
        Match match = new Match(null, 1, "Real Madrid", "Corinthians", request.matchDate(), null, "Open");

        when(matchPort.save(any(Match.class))).thenReturn(match);
        MatchResponse response = useCase.execute(request);

        assertEquals("Real Madrid", response.homeTeam());
        verify(matchValidator).validate(any(Match.class));
        verify(matchPort).save(any(Match.class));
    }
}
