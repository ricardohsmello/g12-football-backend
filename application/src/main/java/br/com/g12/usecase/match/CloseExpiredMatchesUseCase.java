package br.com.g12.usecase.match;

import br.com.g12.model.LiveMatchScore;
import br.com.g12.model.Match;
import br.com.g12.model.Score;
import br.com.g12.port.LiveMatchScorePort;
import br.com.g12.port.MatchPort;

import java.util.Date;
import java.util.List;

public class CloseExpiredMatchesUseCase {

    private final MatchPort matchPort;
    private final LiveMatchScorePort liveMatchScorePort;

    public CloseExpiredMatchesUseCase(MatchPort matchPort, LiveMatchScorePort liveMatchScorePort) {
        this.matchPort = matchPort;
        this.liveMatchScorePort = liveMatchScorePort;
    }

    public int execute() {
        Date now = new Date();

        List<Match> expiredMatches = matchPort.findExpiredOpenMatches(now);

        int closed = matchPort.closeExpiredMatches(now);

        expiredMatches.forEach(match ->
                liveMatchScorePort.findByMatchId(match.getId()).ifPresentOrElse(
                        existing -> {},
                        () -> {
                            Date createdAt = new Date();
                            liveMatchScorePort.save(
                                    new LiveMatchScore(null, match.getId(), match.getCompetitionId(), new Score(0, 0), createdAt, createdAt)
                            );
                        }
                )
        );

        return closed;
    }
}
