package br.com.g12.config;

import br.com.g12.service.PredictionScoringService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {
    @Bean
    public PredictionScoringService scoringService() {
        return new PredictionScoringService();
    }
}
