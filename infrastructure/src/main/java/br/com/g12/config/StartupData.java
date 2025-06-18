package br.com.g12.config;

import br.com.g12.model.Match;
import br.com.g12.port.BetPort;
import br.com.g12.port.MatchPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Configuration
public class StartupData {

    @Bean
    public CommandLineRunner loadData(MatchPort matchPort, BetPort betPort) {
        return args -> {

            var round = 12;

//            createRound(round, "Redbull Bragantino", "Bahia", "2025-06-14T16:00", matchPort, betPort);
//            createRound(round, "Vitória", "Cruzeiro", "2025-06-14T16:00", matchPort, betPort);
//            createRound(round, "Fortaleza", "Santos", "2025-06-14T16:00", matchPort, betPort);
//            createRound(round, "Grêmio", "Corinthians", "2025-06-14T16:00", matchPort, betPort);
//            createRound(round, "São Paulo", "Vasco da Gama", "2025-06-14T16:00", matchPort, betPort);
//            createRound(round, "Atletico Mineiro", "Internacional", "2025-06-14T16:00", matchPort, betPort);
//            createRound(round, "Fluminense", "Ceara", "2025-06-14T16:00", matchPort, betPort);
//            createRound(round, "Botafogo", "Mirassol", "2025-06-14T16:00", matchPort, betPort);
//            createRound(round, "Palmeiras", "Juventude", "2025-06-14T16:00", matchPort, betPort);
//            createRound(round, "Sport", "Flamengo", "2025-06-14T16:00", matchPort, betPort);
//
//            round = 13;
//            createRound(round, "Flamengo", "São Paulo", "2025-07-13T16:00", matchPort, betPort);
//            createRound(round, "Vasco da Gama", "Botafogo", "2025-07-13T16:00", matchPort, betPort);
//            createRound(round, "Corinthians", "Redbull Bragantino", "2025-07-13T16:00", matchPort, betPort);
//            createRound(round, "Santos", "Palmeiras", "2025-07-13T16:00", matchPort, betPort);
//            createRound(round, "Mirassol", "Fluminense", "2025-07-13T16:00", matchPort, betPort);
//            createRound(round, "Cruzeiro", "Grêmio", "2025-07-13T16:00", matchPort, betPort);
//            createRound(round, "Internacional", "Vitória", "2025-07-13T16:00", matchPort, betPort);
//            createRound(round, "Bahia", "Atletico Mineiro", "2025-07-13T16:00", matchPort, betPort);
//            createRound(round, "Fortaleza", "Ceara", "2025-07-13T19:00", matchPort, betPort);
//            createRound(round, "Juventude", "Sport", "2025-07-13T19:00", matchPort, betPort);
//
//            round = 14;
//            createRound(round, "Fluminense", "Cruzeiro", "2025-07-16T16:00", matchPort, betPort);
//            createRound(round, "Botafogo", "Vitória", "2025-07-16T16:00", matchPort, betPort);
//            createRound(round, "Palmeiras", "Mirassol", "2025-07-16T16:00", matchPort, betPort);
//            createRound(round, "Santos", "Flamengo", "2025-07-16T16:00", matchPort, betPort);
//            createRound(round, "Redbull Bragantino", "São Paulo", "2025-07-16T16:00", matchPort, betPort);
//            createRound(round, "Atletico Mineiro", "Sport", "2025-07-16T16:00", matchPort, betPort);
//            createRound(round, "Grêmio", "Fortaleza", "2025-07-16T16:00", matchPort, betPort);
//            createRound(round, "Bahia", "Internacional", "2025-07-16T16:00", matchPort, betPort);
//            createRound(round, "Ceara", "Corinthians", "2025-07-16T19:00", matchPort, betPort);
//            createRound(round, "Juventude", "Vasco da Gama", "2025-07-16T19:00", matchPort, betPort);
//
//            System.out.println("Matches and bets initialized");

        };
    }

    private void createRound(int round, String homeTeam, String awayTeam, String date, MatchPort matchPort, BetPort betPort) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        Match match1 = new Match(null, round, homeTeam, awayTeam, sdf.parse(date), null, "OPEN");
        matchPort.save(match1);
    }
}
