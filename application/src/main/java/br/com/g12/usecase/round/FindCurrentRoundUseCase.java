package br.com.g12.usecase.round;

import br.com.g12.exception.RoundSummaryException;
import br.com.g12.port.MatchPort;
import br.com.g12.usecase.AbstractUseCase;

public class FindCurrentRoundUseCase extends AbstractUseCase<String> {

    private final MatchPort matchPort;

    public FindCurrentRoundUseCase(final MatchPort matchPort) {
        this.matchPort = matchPort;
    }

    public int execute() {
        logInput("Finding current round");

        try {
            return matchPort.findNextOpenRound();
        } catch (RoundSummaryException e) {
            logError(e);
            throw e;
        } finally {
            logSuccess();
        }

    }
}
