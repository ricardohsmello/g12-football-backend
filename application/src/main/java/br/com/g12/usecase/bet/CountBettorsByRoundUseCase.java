package br.com.g12.usecase.bet;

import br.com.g12.exception.BetException;
import br.com.g12.model.CompetitionDefaults;
import br.com.g12.port.BetPort;
import br.com.g12.usecase.AbstractUseCase;

public class CountBettorsByRoundUseCase extends AbstractUseCase<Integer> {

    private final BetPort betPort;

    public CountBettorsByRoundUseCase(BetPort betPort) {
        this.betPort = betPort;
    }

    public int execute(int round) {
        return execute(CompetitionDefaults.DEFAULT_COMPETITION_ID, round);
    }

    public int execute(String competitionId, int round) {
        logInput(round);

        try {
            return betPort.countDistinctUsernamesByCompetitionIdAndRound(CompetitionDefaults.competitionIdOrDefault(competitionId), round);
        } catch (BetException e) {
            logError(e);
            throw e;
        }

    }
}
