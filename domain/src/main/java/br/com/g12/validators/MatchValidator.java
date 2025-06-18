package br.com.g12.validators;

import br.com.g12.exception.MatchException;
import br.com.g12.exception.NotFoundException;
import br.com.g12.model.Match;

import java.util.Date;

public class MatchValidator {

    public void validate(Match match) throws MatchException {

        if (match == null) throw new NotFoundException("Match");

        if (match.getHomeTeam() == null || match.getAwayTeam() == null) {
            throw new MatchException("Teams must not be null");
        }

        if (match.getHomeTeam().equals(match.getAwayTeam())) {
            throw new MatchException("Teams must be different");
        }

        if (match.getMatchDate().before(new Date())) {
            throw new MatchException("Match cannot be scheduled for a past date");
        }

        if (match.getRound() < 0 || match.getRound() > 38) {
            throw new MatchException("Invalid round: must be between 1 and 38");
        }

    }
}
