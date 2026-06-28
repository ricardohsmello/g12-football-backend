package br.com.g12.usecase.live;

import br.com.g12.model.LiveMatchScore;
import br.com.g12.model.Match;
import br.com.g12.model.Score;
import br.com.g12.port.LiveMatchScorePort;
import br.com.g12.port.MatchPort;
import br.com.g12.usecase.AbstractUseCase;

import java.util.Date;
import java.util.Optional;

public class UpdateLiveScoreUseCase extends AbstractUseCase<Void> {

    private final MatchPort matchPort;
    private final LiveMatchScorePort liveMatchScorePort;

    public UpdateLiveScoreUseCase(MatchPort matchPort, LiveMatchScorePort liveMatchScorePort) {
        this.matchPort = matchPort;
        this.liveMatchScorePort = liveMatchScorePort;
    }

    public void execute(String matchId, Score score) {
        Match match = matchPort.find(matchId);
        if (match == null) {
            throw new IllegalArgumentException("Match not found: " + matchId);
        }

        Optional<LiveMatchScore> existing = liveMatchScorePort.findByMatchId(matchId);

        Date now = new Date();
        Date createdAt = existing.map(LiveMatchScore::getCreatedAt).orElse(now);
        String id = existing.map(LiveMatchScore::getId).orElse(null);

        liveMatchScorePort.save(new LiveMatchScore(id, matchId, match.getCompetitionId(), score, createdAt, now));

        log.info("Live score updated for match {} -> {}x{}", matchId, score.homeTeam(), score.awayTeam());
    }
}
