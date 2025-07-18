package br.com.g12.scheduler;

import br.com.g12.usecase.match.CloseExpiredMatchesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CloseMatchesScheduler {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CloseExpiredMatchesUseCase closeExpiredMatchesUseCase;

    public CloseMatchesScheduler(CloseExpiredMatchesUseCase useCase) {
        this.closeExpiredMatchesUseCase = useCase;
    }

    @Scheduled(cron = "0 0,30 11,16,17,18,19,20,21,22 * * *", zone = "America/Sao_Paulo")
    public void run() {
        int closed = closeExpiredMatchesUseCase.execute();
        log.info("Closing Matches: {}", closed);
    }
}