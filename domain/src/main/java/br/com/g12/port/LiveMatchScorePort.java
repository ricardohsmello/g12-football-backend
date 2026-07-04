package br.com.g12.port;

import br.com.g12.model.LiveMatchScore;

import java.util.List;
import java.util.Optional;

public interface LiveMatchScorePort {
    LiveMatchScore save(LiveMatchScore liveMatchScore);
    Optional<LiveMatchScore> findByMatchId(String matchId);
    List<LiveMatchScore> findByMatchIdIn(List<String> matchIds);
    void deleteByMatchId(String matchId);
}
