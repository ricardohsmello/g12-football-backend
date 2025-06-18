package br.com.g12.validators;

import br.com.g12.exception.MatchException;
import br.com.g12.fake.MatchFake;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class MatchValidatorTest {
    @Test
    public void should_thrown_error_when_match_has_same_teams() {
        var match = MatchFake.builder()
                .withSameTeams("Corinthians")
                .build();

        MatchValidator validator = new MatchValidator();

        MatchException ex = assertThrows(MatchException.class, () -> validator.validate(match));
        assertEquals("Teams must be different", ex.getMessage());
    }

    @Test
    public void should_throw_error_when_match_has_null_teams() {
        var match = MatchFake.builder()
                .withNullTeams()
                .build();

        MatchValidator validator = new MatchValidator();
        MatchException matchException = assertThrows(MatchException.class, () -> validator.validate(match));

        assertEquals("Teams must not be null", matchException.getMessage());
    }

    @Test
    public void should_throw_error_when_match_date_is_in_the_past() {
        var date = getDateWithOffset(-1);

        var pastMatch = MatchFake.builder()
                .matchDate(date)
                .build();

        MatchValidator validator = new MatchValidator();
        MatchException matchException = assertThrows(MatchException.class, () -> validator.validate(pastMatch));
        assertEquals("Match cannot be scheduled for a past date", matchException.getMessage());
    }

    @Test
    public void should_throw_error_when_match_date_is_in_the_future() {
        var match = MatchFake.builder()
                .round(39)
                .matchDate(getDateWithOffset(1))
                .build();

        MatchValidator validator = new MatchValidator();
        MatchException matchException = assertThrows(MatchException.class, () -> validator.validate(match));
        assertEquals("Invalid round: must be between 1 and 38", matchException.getMessage());
    }

    @Test
    public void should_validate_match() {
        var match = MatchFake.builder()
                .homeTeam("Corinthians")
                .awayTeam("Barcelona")
                .round(5)
                .matchDate(getDateWithOffset(5))
                .build();

        MatchValidator validator = new MatchValidator();
        assertDoesNotThrow(() -> validator.validate(match));
    }

    private Date getDateWithOffset(int days) {
        var calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }
}
