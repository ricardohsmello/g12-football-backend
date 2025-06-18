package br.com.g12.usecase.match;

import br.com.g12.exception.FindMatchesWithUserException;
import br.com.g12.model.MatchWithPrediction;
import br.com.g12.port.MatchPort;
import br.com.g12.request.UserRoundRequest;
import br.com.g12.response.MatchResponse;
import br.com.g12.usecase.AbstractUseCase;

import java.util.List;

public class FindMatchesWithUserBetsUseCase extends AbstractUseCase<UserRoundRequest> {

    private final MatchPort matchPort;

    public FindMatchesWithUserBetsUseCase(final MatchPort matchPort) {
        this.matchPort = matchPort;
    }

    public List<MatchResponse> execute(UserRoundRequest userRoundRequest) {
        logInput(userRoundRequest);

        try {
            validate(userRoundRequest);

            List<MatchWithPrediction> matchWithPredictionList = matchPort.findByRoundUser(userRoundRequest.username(), userRoundRequest.round());

            logSuccess();
            return matchWithPredictionList.stream().map(MatchResponse::fromModel).toList();
        } catch (FindMatchesWithUserException e) {
            logError(e);
            throw e;
        }
    }

    private void validate(UserRoundRequest userRoundRequest) throws FindMatchesWithUserException {
        if (userRoundRequest.username() == null || userRoundRequest.username().isEmpty()) {
            throw new FindMatchesWithUserException("Username is required");
        }

        if (userRoundRequest.round() < 0 || userRoundRequest.round() > 38) {
            throw new FindMatchesWithUserException("Round must be between 0 and 38");
        }
    }
}
