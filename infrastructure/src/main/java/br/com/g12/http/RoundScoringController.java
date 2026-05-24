package br.com.g12.http;

import br.com.g12.usecase.bet.ScoreBetsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/rounds-scoring")
public class RoundScoringController {

    private final ScoreBetsUseCase scoreBetsUseCase;

    public RoundScoringController(ScoreBetsUseCase scoreBetsUseCase) {
        this.scoreBetsUseCase = scoreBetsUseCase;
    }

    @PutMapping("/{round}/score")
    public ResponseEntity<Void> scoreBets(
            @PathVariable int round,
            @RequestParam(value = "year", required = false) Integer year) {
//        for (int i = 1; i < 39; i++) {
//            scoreBetsUseCase.execute(i);
//        }
        int scoreYear = year != null ? year : LocalDate.now().getYear();
        scoreBetsUseCase.execute(round, scoreYear);
        return ResponseEntity.noContent().build();
    }
}
