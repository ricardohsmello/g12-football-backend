package br.com.g12.usecase.match;

import br.com.g12.exception.MatchException;
import br.com.g12.model.Match;
import br.com.g12.port.MatchPort;
import br.com.g12.request.MatchRequest;
import br.com.g12.response.MatchResponse;
import br.com.g12.usecase.AbstractUseCase;
import br.com.g12.validators.MatchValidator;

public class CreateMatchUseCase extends AbstractUseCase<MatchRequest> {

    private final MatchValidator validator;
    private final MatchPort matchPort;

    public CreateMatchUseCase(MatchPort matchPort, MatchValidator validator) {
        this.matchPort = matchPort;
        this.validator = validator;
    }

    public MatchResponse execute(MatchRequest matchRequest) {
        logInput(matchRequest);

        try {
            Match match = matchRequest.toModel();

            validator.validate(match);
            Match save = matchPort.save(match);

            logSuccess();
            return MatchResponse.fromModel(save);

        } catch (MatchException e) {
            logError(e);
            throw e;
        }

    }
}
