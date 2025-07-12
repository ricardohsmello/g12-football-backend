package br.com.g12.usecase.bet;

import br.com.g12.port.BetPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CountBettorsByRoundUseCaseTest {

    private final BetPort betPort = mock(BetPort.class);

    @Test
    public void should_return_count_of_bettors_successfully() {
        var totalBettors = 8;

        CountBettorsByRoundUseCase createBetUseCase = new CountBettorsByRoundUseCase(betPort);

        when(betPort.countDistinctUsernamesByRound(13)).thenReturn(totalBettors);
        int execute = createBetUseCase.execute(13);

        assertEquals(execute, totalBettors);

    }
}
