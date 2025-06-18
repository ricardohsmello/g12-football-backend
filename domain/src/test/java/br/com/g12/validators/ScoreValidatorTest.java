package br.com.g12.validators;

import br.com.g12.exception.NotFoundException;
import br.com.g12.exception.ScoreException;
import br.com.g12.model.Score;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScoreValidatorTest {


    @Test
    public void should_throw_exception_when_score_is_null() {
        Score score = null;

        ScoreValidator validator = new ScoreValidator();
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> validator.validate(score));
        assertEquals("Score not found!", notFoundException.getMessage());
    }

    @Test
    public void should_throw_exception_score_is_incorrect() {
        Score score = new Score(-1, 5);

        ScoreValidator validator = new ScoreValidator();

        ScoreException scoreException = assertThrows(ScoreException.class, () -> validator.validate(score));
        assertEquals("Score can't be less than 0", scoreException.getMessage());
    }

}
