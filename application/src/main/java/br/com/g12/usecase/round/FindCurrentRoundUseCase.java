package br.com.g12.usecase.round;

import br.com.g12.exception.RoundSummaryException;
import br.com.g12.model.CompetitionDefaults;
import br.com.g12.port.MatchPort;
import br.com.g12.usecase.AbstractUseCase;

public class FindCurrentRoundUseCase extends AbstractUseCase<String> {

    private final MatchPort matchPort;

    public FindCurrentRoundUseCase(final MatchPort matchPort) {
        this.matchPort = matchPort;
    }

    public int execute() {
        return execute(CompetitionDefaults.DEFAULT_COMPETITION_ID);
    }

    public int execute(String competitionId) {
        logInput("Finding current round");

        try {
            return matchPort.findNextOpenRound(CompetitionDefaults.competitionIdOrDefault(competitionId));
        } catch (RoundSummaryException e) {
            logError(e);
            throw e;
        } finally {
            logSuccess();
        }

    }
}
