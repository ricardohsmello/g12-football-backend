package br.com.g12.http;

import br.com.g12.usecase.bet.ScoreBetsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rounds-scoring")
public class RoundScoringController {

    private final ScoreBetsUseCase scoreBetsUseCase;

    public RoundScoringController(ScoreBetsUseCase scoreBetsUseCase) {
        this.scoreBetsUseCase = scoreBetsUseCase;
    }

    @PutMapping("/{round}/score")
    public ResponseEntity<Void> scoreBets(@PathVariable int round) {
//        for (int i = 1; i < 39; i++) {
//            scoreBetsUseCase.execute(i);
//        }
        scoreBetsUseCase.execute(round);
        return ResponseEntity.noContent().build();
    }
}