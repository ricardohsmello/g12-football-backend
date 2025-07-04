package br.com.g12.http;

import br.com.g12.usecase.round.FindNextOpenRoundUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/round")
public class RoundController {

    private final FindNextOpenRoundUseCase findNextOpenRoundUseCase;

    RoundController(final FindNextOpenRoundUseCase findNextOpenRoundUseCase) {
        this.findNextOpenRoundUseCase = findNextOpenRoundUseCase;
    }

    @GetMapping("/current")
    public int getCurrentRound() {
        return findNextOpenRoundUseCase.execute();
    }
}
