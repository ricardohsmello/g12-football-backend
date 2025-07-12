package br.com.g12.usecase.bet;

import br.com.g12.exception.BetException;
import br.com.g12.port.BetPort;
import br.com.g12.usecase.AbstractUseCase;

public class CountBettorsByRoundUseCase extends AbstractUseCase<Integer> {

    private final BetPort betPort;

    public CountBettorsByRoundUseCase(BetPort betPort) {
        this.betPort = betPort;
    }

    public int execute(int round) {
        logInput(round);

        try {
            return betPort.countDistinctUsernamesByRound(round);
        } catch (BetException e) {
            logError(e);
            throw e;
        }

    }
}
