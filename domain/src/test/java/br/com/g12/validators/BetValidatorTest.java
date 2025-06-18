package br.com.g12.validators;

import br.com.g12.exception.BetException;
import br.com.g12.exception.NotFoundException;
import br.com.g12.fake.BetFake;
import br.com.g12.fake.MatchFake;
import br.com.g12.model.Bet;
import br.com.g12.model.Match;
import br.com.g12.model.Score;
import br.com.g12.port.MatchPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BetValidatorTest {

    private BetValidator validator;
    private MatchPort matchPort;

    @BeforeEach
    void setup() {
        matchPort = mock(MatchPort.class);
        validator = new BetValidator(matchPort);
    }

    @Test
    public void should_throw_exception_when_bet_is_null() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> validator.validate(null));
        assertEquals("Bet not found!", notFoundException.getMessage());
    }

    @Test
    public void should_throw_error_when_match_id_is_null() {
        Bet build = BetFake.builder()
                .id(null)
                .build();

        BetException betException = assertThrows(BetException.class, () -> validator.validate(build));
        assertEquals(betException.getMessage(), "Match ID is required");
    }

    @Test
    public void should_throw_error_when_match_idUser_is_null_or_empty() {
        Bet build = BetFake.builder()
                .matchId("123")
                .username("")
                .build();

        BetException betException = assertThrows(BetException.class, () -> validator.validate(build));
        assertEquals(betException.getMessage(), "User ID is required");
    }

    @Test
    public void should_throw_error_when_prediction_is_not_provided() {
        Bet build = BetFake.builder()
                .matchId("123")
                .username("1")
                .prediction(null)
                .build();

        BetException betException = assertThrows(BetException.class, () -> validator.validate(build));
        assertEquals(betException.getMessage(), "Score prediction must be provided");
    }

    @Test
    public void should_throw_error_when_round_is_invalid() {
        Bet build = BetFake.builder()
                .matchId("123")
                .username("1")
                .prediction(new Score(2, 2))
                .round(-1)
                .build();

        BetException betException = assertThrows(BetException.class, () -> validator.validate(build));
        assertEquals(betException.getMessage(), "Round must be between 1 and 38");
    }

    @Test
    public void should_throw_error_when_match_is_closed() {
        var matchId = "123";

        Bet build = BetFake.builder()
                .matchId(matchId)
                .username("ricas")
                .prediction(new Score(2, 2))
                .round(21)
                .build();

        Match matchClosed = MatchFake.builder()
                .id(matchId)
                .homeTeam("Corinthians")
                .awayTeam("Cruzeiro")
                .matchDate(getDateWithOffset(5))
                .status("Closed")
                .build();

        when((matchPort.find(any()))).thenReturn(matchClosed);

        BetException betException = assertThrows(BetException.class, () -> validator.validate(build));
        assertEquals(betException.getMessage(), "Cannot place a bet on a closed match!");
    }

    @Test
    public void should_throw_error_when_bet_time_is_greater_than_match_game() {
        var matchId = "123";
        var matchTime = getDate(18, 30, 0);
        var betTime = getDate(18, 30, 1);

        Bet build = BetFake.builder()
                .matchId(matchId)
                .username("ricas")
                .prediction(new Score(2, 2))
                .date(betTime)
                .round(21)
                .build();

        Match matchClosed = MatchFake.builder()
                .id(matchId)
                .homeTeam("Juventude")
                .awayTeam("Mirassol")
                .matchDate(matchTime)
                .status("Open")
                .build();

        when((matchPort.find(any()))).thenReturn(matchClosed);

        BetException betException = assertThrows(BetException.class, () -> validator.validate(build));
        assertEquals(betException.getMessage(), "Bet is Closed!");
    }

    @Test
    public void should_validate_match() {
        var matchId = "123";
        var matchTime = getDate(16, 0, 0);
        var betTime = getDate(15, 59, 59);

        Bet build = BetFake.builder()
                .matchId(matchId)
                .username("ricas")
                .prediction(new Score(1, 2))
                .date(betTime)
                .round(21)
                .build();

        var match = MatchFake.builder()
                .homeTeam("Corinthians")
                .awayTeam("Barcelona")
                .round(5)
                .matchDate(matchTime)
                .status("Open")
                .build();

        when((matchPort.find(any()))).thenReturn(match);
        assertDoesNotThrow(() -> validator.validate(build));
    }

    private Date getDateWithOffset(int days) {
        var calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    private Date getDate(int hour, int minute, int second) {
        var calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
