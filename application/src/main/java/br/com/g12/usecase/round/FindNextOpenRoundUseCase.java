package br.com.g12.usecase.round;

import br.com.g12.exception.RoundSummaryException;
import br.com.g12.port.MatchPort;
import br.com.g12.usecase.AbstractUseCase;

public class FindNextOpenRoundUseCase extends AbstractUseCase<String> {

    private final MatchPort matchPort;

    public FindNextOpenRoundUseCase(final MatchPort matchPort) {
        this.matchPort = matchPort;
    }

    public int execute() {
        logInput("Finding round");

        try {
            return matchPort.findNextMatchRound();
        } catch (RoundSummaryException e) {
            logError(e);
            throw e;
        } finally {
            logSuccess();
        }

    }
}
