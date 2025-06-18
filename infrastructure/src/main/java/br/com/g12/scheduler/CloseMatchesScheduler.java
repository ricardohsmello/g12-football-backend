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

//    @Scheduled(cron = "0 20 10,15,16,18,20,21 * * *", zone = "America/Sao_Paulo")
    @Scheduled(cron = "0 36 20 * * *", zone = "America/Sao_Paulo")
    public void run() {
        int closed = closeExpiredMatchesUseCase.execute();
        log.info("Closing Matches: {}", closed);
    }
}