package br.com.g12.usecase.bet;

import br.com.g12.exception.BetException;
import br.com.g12.model.Bet;
import br.com.g12.port.BetPort;
import br.com.g12.request.BetRequest;
import br.com.g12.response.BetResponse;
import br.com.g12.usecase.AbstractUseCase;
import br.com.g12.validators.BetValidator;

public class CreateBetUseCase extends AbstractUseCase<BetRequest> {

    private final BetPort betPort;
    private final BetValidator betValidator;

    public CreateBetUseCase(BetPort betPort, BetValidator betValidator) {
        this.betPort = betPort;
        this.betValidator = betValidator;
    }

    public BetResponse execute(BetRequest betRequest) {
        logInput(betRequest);

        try {
            Bet bet = betRequest.toModel();
            betValidator.validate(bet);
            Bet saved = betPort.save(bet);
            logSuccess();
            return BetResponse.fromModel(saved);

        } catch (BetException e) {
            logError(e);
            throw e;
        }
    }
}
