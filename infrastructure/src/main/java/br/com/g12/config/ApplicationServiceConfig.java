package br.com.g12.config;

import br.com.g12.service.ScoringService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {
    @Bean
    public ScoringService scoringService() {
        return new ScoringService();
    }
}
