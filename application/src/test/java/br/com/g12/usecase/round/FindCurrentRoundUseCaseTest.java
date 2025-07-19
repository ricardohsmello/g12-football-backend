package br.com.g12.usecase.round;

import br.com.g12.exception.RoundSummaryException;
import br.com.g12.port.MatchPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class FindCurrentRoundUseCaseTest {

    private final MatchPort matchPort = mock(MatchPort.class);

    @Test
    public void should_throw_exception_when_find_current_round() {

        FindCurrentRoundUseCase useCase = new FindCurrentRoundUseCase(matchPort);
        when(matchPort.findNextOpenRound()).thenThrow(new RoundSummaryException("Some error"));

        RoundSummaryException thrown = assertThrows(RoundSummaryException.class, useCase::execute);
        assertEquals("Some error", thrown.getMessage());
        verify(matchPort).findNextOpenRound();
    }

    @Test
    public void should_return_round_successfully() {
        var round = 13;

        FindCurrentRoundUseCase useCase = new FindCurrentRoundUseCase(matchPort);
        when(matchPort.findNextOpenRound()).thenReturn(round);
        var result = useCase.execute();
        assertEquals(round, result);

        verify(matchPort).findNextOpenRound();
    }
}
