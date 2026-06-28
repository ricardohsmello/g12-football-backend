package br.com.g12.http;

import br.com.g12.model.LiveMatchScoreboard;
import br.com.g12.model.Score;
import br.com.g12.usecase.live.GetLiveScoreboardUseCase;
import br.com.g12.usecase.live.UpdateLiveScoreUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/live-scoring")
public class LiveScoringController {

    private final UpdateLiveScoreUseCase updateLiveScoreUseCase;
    private final GetLiveScoreboardUseCase getLiveScoreboardUseCase;

    public LiveScoringController(UpdateLiveScoreUseCase updateLiveScoreUseCase,
                                 GetLiveScoreboardUseCase getLiveScoreboardUseCase) {
        this.updateLiveScoreUseCase = updateLiveScoreUseCase;
        this.getLiveScoreboardUseCase = getLiveScoreboardUseCase;
    }

    @PutMapping("/matches/{matchId}/score")
    public ResponseEntity<Void> updateLiveScore(
            @PathVariable String matchId,
            @RequestBody Score score) {
        updateLiveScoreUseCase.execute(matchId, score);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<LiveMatchScoreboard>> getLiveScoreboard(
            @RequestParam(value = "competitionId", required = false) String competitionId,
            @RequestParam int round) {
        List<LiveMatchScoreboard> scoreboard = getLiveScoreboardUseCase.execute(competitionId, round);
        return ResponseEntity.ok(scoreboard);
    }
}
