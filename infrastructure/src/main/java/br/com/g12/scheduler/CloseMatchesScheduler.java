package br.com.g12.scheduler;

import br.com.g12.usecase.match.CloseExpiredMatchesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CloseMatchesScheduler {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CloseExpiredMatchesUseCase closeExpiredMatchesUseCase;

    public CloseMatchesScheduler(CloseExpiredMatchesUseCase useCase) {
        this.closeExpiredMatchesUseCase = useCase;
    }

//    @Scheduled(
//            fixedRate = 5,
//            timeUnit = TimeUnit.SECONDS
//    )
    @Scheduled(
            cron = "0 0,30 0,1,11,13,14,16,17,18,19,20,21,22,23 * * *",
            zone = "America/Sao_Paulo"
    )
    public void run() {
        int closed = closeExpiredMatchesUseCase.execute();
        log.info("Closing Matches: {}", closed);
    }


}