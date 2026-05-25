package br.com.g12.validators;

import br.com.g12.exception.BetException;
import br.com.g12.exception.NotFoundException;
import br.com.g12.model.Bet;
import br.com.g12.model.Match;
import br.com.g12.model.Score;
import br.com.g12.port.MatchPort;

public class BetValidator {

    private final MatchPort matchPort;

    public BetValidator(MatchPort matchPort) {
        this.matchPort = matchPort;
    }

    public void validate(Bet bet) {
        if (bet == null) {
            throw new NotFoundException("Bet");
        }

        if (bet.getMatchId() == null || bet.getMatchId().isBlank()) {
            throw new BetException("Match ID is required");
        }

        if (bet.getUsername() == null || bet.getUsername().isBlank()) {
            throw new BetException("User ID is required");
        }

        if (bet.getCompetitionId() == null || bet.getCompetitionId().isBlank()) {
            throw new BetException("Competition ID is required");
        }

        if (bet.getPrediction() == null) {
            throw new BetException("Score prediction must be provided");
        }

        if (bet.getRound() < 1) {
            throw new BetException("Round must be greater than or equal to 1");
        }

        validateMatch(bet);
        validateScore(bet.getPrediction());
    }

    private void validateMatch(Bet bet) {
        Match match = matchPort.find(bet.getMatchId());

        if (match == null) {
            throw new NotFoundException("Match");
        }

        if (!bet.getCompetitionId().equals(match.getCompetitionId())) {
            throw new BetException("Bet competition must match the match competition");
        }

        if (bet.getRound() != match.getRound()) {
            throw new BetException("Bet round must match the match round");
        }

        if (match.getStatus().equals("Closed")) {
            throw new BetException("Cannot place a bet on a closed match!");
        }

        if (bet.getDate().after(match.getMatchDate())) {
            throw new BetException("Bet is Closed!");
        }

    }

    private void validateScore(Score score) {
        if (score.homeTeam() < 0 || score.awayTeam() < 0) {
            throw new BetException("Score cannot be negative");
        }
    }
}
