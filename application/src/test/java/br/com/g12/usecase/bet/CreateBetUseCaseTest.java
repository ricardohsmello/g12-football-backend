package br.com.g12.usecase.bet;

import br.com.g12.exception.BetException;
import br.com.g12.model.Bet;
import br.com.g12.model.Score;
import br.com.g12.port.BetPort;
import br.com.g12.request.BetRequest;
import br.com.g12.response.BetResponse;
import br.com.g12.validators.BetValidator;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateBetUseCaseTest {

    private final BetPort betPort = mock(BetPort.class);
    private final BetValidator betValidator = mock(BetValidator.class);


    @Test
    public void should_throw_exception_when_bet_is_invalid() {
        BetRequest invalidRequest = new BetRequest("user1", "match1", new Score(-1, 3), 5);
        doThrow(new BetException("Score cannot be negative"))
                .when(betValidator).validate(any());

        CreateBetUseCase useCase = new CreateBetUseCase(betPort, betValidator);

        BetException ex = assertThrows(BetException.class, () -> useCase.execute(invalidRequest));

        assertEquals("Score cannot be negative", ex.getMessage());

        verify(betValidator).validate(any());
        verify(betPort, never()).save(any());
    }

    @Test
    public void should_create_bet_successfully() {
        var betRequest = new BetRequest("1", "1", new Score(2, 3), 5);
        Bet bet = new Bet("001", "1", "1", new Score(2, 3), 5, null, new Date());
        when(betPort.save(any(Bet.class))).thenReturn(bet);

        CreateBetUseCase createBetUseCase = new CreateBetUseCase(betPort, betValidator);
        BetResponse response = createBetUseCase.execute(betRequest);

        assertEquals(bet.getId(), response.id());

        verify(betValidator).validate(any(Bet.class));
        verify(betPort).save(any(Bet.class));
    }
}
