package br.com.g12.usecase.match;

import br.com.g12.port.MatchPort;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CloseExpiredMatchesUseCaseTest {

    private final MatchPort matchPort = mock(MatchPort.class);
    private final CloseExpiredMatchesUseCase useCase = new CloseExpiredMatchesUseCase(matchPort);

    @Test
    public void should_execute_schedule_successfully() {

        when(matchPort.closeExpiredMatches(any(Date.class))).thenReturn(5);

        int result = useCase.execute();

        assertEquals(5, result);
        verify(matchPort).closeExpiredMatches(any(Date.class));

    }
}
