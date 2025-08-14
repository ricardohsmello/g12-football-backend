package br.com.g12.config;

import br.com.g12.port.ScoreboardPort;
import br.com.g12.service.PredictionScoringService;
import br.com.g12.service.RagIngestDataService;
import br.com.g12.service.RoundScoreboardService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {

    @Bean
    public PredictionScoringService scoringService() {
        return new PredictionScoringService();
    }

    @Bean
    public RoundScoreboardService roundScoreboardService(ScoreboardPort scoreboardPort) {
        return new RoundScoreboardService(scoreboardPort);
    }

    @Bean
    public RagIngestDataService ragIngestDataService() {
        return new RagIngestDataService();
    }

}
