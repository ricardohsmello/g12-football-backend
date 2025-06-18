package br.com.g12.usecase.match;

import br.com.g12.port.MatchPort;

import java.util.Date;

public class CloseExpiredMatchesUseCase {

    private final MatchPort matchPort;

    public CloseExpiredMatchesUseCase(MatchPort matchPort) {
        this.matchPort = matchPort;
    }

    public int execute() {
        return matchPort.closeExpiredMatches(new Date());
    }
}
