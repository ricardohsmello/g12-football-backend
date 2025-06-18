package br.com.g12.validators;

import br.com.g12.exception.NotFoundException;
import br.com.g12.exception.ScoreException;
import br.com.g12.model.Score;

public class ScoreValidator {

    public void validate(Score score) throws ScoreException {
        if (score == null) {
            throw new NotFoundException("Score");
        }

        if (score.homeTeam() < 0 || score.awayTeam() < 0) {
            throw new ScoreException("Score can't be less than 0");
        }
    }
}
