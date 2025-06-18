package br.com.g12.http;

import br.com.g12.request.BetRequest;
import br.com.g12.response.BetResponse;
import br.com.g12.usecase.bet.CreateBetUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bet")
public class BetController {

    private final CreateBetUseCase createBetUseCase;

    BetController(CreateBetUseCase createBetUseCase) {
      this.createBetUseCase = createBetUseCase;
    }

    @PostMapping
    public ResponseEntity<BetResponse> addBet(@RequestBody BetRequest betRequest) {
        BetResponse betResponse = createBetUseCase.execute(betRequest);
        return ResponseEntity.ok(betResponse);
    }
}
