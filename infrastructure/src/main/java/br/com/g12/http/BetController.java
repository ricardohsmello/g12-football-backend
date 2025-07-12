package br.com.g12.http;

import br.com.g12.request.BetRequest;
import br.com.g12.response.BetResponse;
import br.com.g12.usecase.bet.CountBettorsByRoundUseCase;
import br.com.g12.usecase.bet.CreateBetUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bet")
public class BetController {

    private final CreateBetUseCase createBetUseCase;
    private final CountBettorsByRoundUseCase countBettorsByRoundUseCase;

    BetController(CreateBetUseCase createBetUseCase, CountBettorsByRoundUseCase countBettorsByRoundUseCase) {
      this.createBetUseCase = createBetUseCase;
      this.countBettorsByRoundUseCase = countBettorsByRoundUseCase;
    }

    @PostMapping
    public ResponseEntity<BetResponse> addBet(@RequestBody BetRequest betRequest) {
        BetResponse betResponse = createBetUseCase.execute(betRequest);
        return ResponseEntity.ok(betResponse);
    }

    @GetMapping("/round/{round}/bettors-count")
    public ResponseEntity<Integer> countBettorsByRound(@PathVariable int round) {
        int count = countBettorsByRoundUseCase.execute(round);
        return ResponseEntity.ok(count);
    }
}
