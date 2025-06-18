package br.com.g12.http;

import br.com.g12.response.ScoreboardResponse;
import br.com.g12.usecase.score.ScoreBoardUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/scoreboard")
public class ScoreboardController {

    private final ScoreBoardUseCase scoreBoardUseCase;

    public ScoreboardController(ScoreBoardUseCase scoreBoardUseCase) {
        this.scoreBoardUseCase = scoreBoardUseCase;
    }

    @GetMapping
    public List<ScoreboardResponse> getByRound(@RequestParam("round") int round) {
        return scoreBoardUseCase.execute(round).stream()
                .map(ScoreboardResponse::fromModel)
                .toList();
    }
}
