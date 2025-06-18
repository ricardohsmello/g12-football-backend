package br.com.g12.usecase.match;

import br.com.g12.exception.FindMatchesWithUserException;
import br.com.g12.model.MatchWithPrediction;
import br.com.g12.port.MatchPort;
import br.com.g12.request.UserRoundRequest;
import br.com.g12.response.MatchResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class FindMatchesWithUserBetsUseCaseTest {

    private final MatchPort matchPort = mock(MatchPort.class);
    private final FindMatchesWithUserBetsUseCase useCase = new FindMatchesWithUserBetsUseCase(matchPort);

    @Test
    public void should_create_match_successfully() {
        UserRoundRequest request = new UserRoundRequest("ricas", 32);

        List<MatchWithPrediction> mockResult = List.of(
                new MatchWithPrediction()
        );

        when(matchPort.findByRoundUser("ricas", 32)).thenReturn(mockResult);

        List<MatchResponse> result = useCase.execute(request);

        verify(matchPort).findByRoundUser("ricas", 32);
        assertEquals(mockResult.size(), result.size());
    }

    @Test
    public void shouldThrowExceptionWhenUsernameIsNull() {
        UserRoundRequest request = new UserRoundRequest(null, 10);

        FindMatchesWithUserException exception = assertThrows(
                FindMatchesWithUserException.class,
                () -> useCase.execute(request)
        );

        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenRoundIsInvalid() {
        UserRoundRequest request = new UserRoundRequest("ricas", 100);

        FindMatchesWithUserException exception = assertThrows(
                FindMatchesWithUserException.class,
                () -> useCase.execute(request)
        );

        assertEquals("Round must be between 0 and 38", exception.getMessage());
    }
}
