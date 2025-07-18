package br.com.g12.config;

import br.com.g12.port.BetPort;
import br.com.g12.port.MatchPort;
import br.com.g12.port.ScoreboardPort;
import br.com.g12.service.PredictionScoringService;
import br.com.g12.service.RoundScoreboardService;
import br.com.g12.usecase.bet.CountBettorsByRoundUseCase;
import br.com.g12.usecase.bet.CreateBetUseCase;
import br.com.g12.usecase.bet.ScoreBetsUseCase;
import br.com.g12.usecase.match.CloseExpiredMatchesUseCase;
import br.com.g12.usecase.match.CreateMatchUseCase;
import br.com.g12.usecase.match.FindMatchesWithUserBetsUseCase;
import br.com.g12.usecase.match.UpdateMatchScoreUseCase;
import br.com.g12.usecase.round.FindCurrentRoundUseCase;
import br.com.g12.usecase.score.ScoreBoardUseCase;
import br.com.g12.validators.BetValidator;
import br.com.g12.validators.MatchValidator;
import br.com.g12.validators.ScoreValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationUseCaseConfig {

    @Bean
    public MatchValidator matchValidator() {
        return new MatchValidator();
    }

    @Bean
    public ScoreValidator scoreValidator() {
        return new ScoreValidator();
    }

    @Bean
     public BetValidator betValidator(MatchPort matchPort) {
        return new BetValidator(matchPort);
    }

    @Bean
    public CreateMatchUseCase createMatchUseCase(MatchPort matchPort, MatchValidator matchValidator) {
        return new CreateMatchUseCase(matchPort, matchValidator);
    }

    @Bean
    public UpdateMatchScoreUseCase updateMatchScoreUseCase(MatchPort matchPort, ScoreValidator scoreValidator) {
        return new UpdateMatchScoreUseCase(matchPort, scoreValidator);
    }

    @Bean
    public CreateBetUseCase createBetUseCase(BetPort betPort, BetValidator betValidator) {
        return new CreateBetUseCase(betPort, betValidator);
    }

    @Bean
    public ScoreBetsUseCase scoreBetsUseCase(MatchPort matchPort, BetPort betPort, PredictionScoringService predictionScoringService, RoundScoreboardService roundScoreboardService) {
        return new ScoreBetsUseCase(matchPort, betPort, predictionScoringService, roundScoreboardService);
    }

    @Bean
    public FindMatchesWithUserBetsUseCase findMatchesWithUserBets(MatchPort matchPort) {
        return new FindMatchesWithUserBetsUseCase(matchPort);
    }

    @Bean
    public CloseExpiredMatchesUseCase closeExpiredMatchesUseCase(MatchPort matchPort) {
        return new CloseExpiredMatchesUseCase(matchPort);
    }

    @Bean
    public ScoreBoardUseCase scoreBoardUseCase(ScoreboardPort scoreboardPort) {
        return new ScoreBoardUseCase(scoreboardPort);
    }

    @Bean
    public FindCurrentRoundUseCase findNextOpenRoundUseCase(MatchPort matchPort) {
        return new FindCurrentRoundUseCase(matchPort);
    }

    @Bean
    public CountBettorsByRoundUseCase countBettorsByRoundUseCase(BetPort betPort) {
        return new CountBettorsByRoundUseCase(betPort);
    }

    @Bean
    public RoundScoreboardService roundScoreboardService(ScoreboardPort scoreboardPort) {
        return new RoundScoreboardService(scoreboardPort);
    }

}
