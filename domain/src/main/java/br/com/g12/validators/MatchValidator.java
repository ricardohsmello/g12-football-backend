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

        if (match.getCompetitionId() == null || match.getCompetitionId().isBlank()) {
            throw new MatchException("Competition ID is required");
        }

        if (match.getStage() == null || match.getStage().isBlank()) {
            throw new MatchException("Stage is required");
        }

        if (match.getRound() < 1) {
            throw new MatchException("Invalid round: must be greater than or equal to 1");
        }

    }
}
