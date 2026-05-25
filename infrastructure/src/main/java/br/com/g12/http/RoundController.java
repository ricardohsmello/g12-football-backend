package br.com.g12.http;

import br.com.g12.usecase.round.FindCurrentRoundUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/round")
public class RoundController {

    private final FindCurrentRoundUseCase findCurrentRoundUseCase;

    RoundController(final FindCurrentRoundUseCase findCurrentRoundUseCase) {
        this.findCurrentRoundUseCase = findCurrentRoundUseCase;
    }

    @GetMapping("/current")
    public int getCurrentRound(@RequestParam(value = "competitionId", required = false) String competitionId) {
        return findCurrentRoundUseCase.execute(competitionId);
    }
}
