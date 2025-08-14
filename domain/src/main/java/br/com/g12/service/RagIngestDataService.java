package br.com.g12.service;

import br.com.g12.model.RagIngestData;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class RagIngestDataService {

    private int predictedHome;
    private int predictedAway;
    private int actualHome;
    private int actualAway;
    private String homeTeam;
    private String awayTeam;
    private String username;
    private int round;
    private boolean hitExactScore;
    private boolean hitOutcome;
    private String actualOutcome;
    private String predictedOutcome;
    private int pointsEarned;
    private int roundPoints;
    private int totalPoints;
    private Set<String> phrases;

    public RagIngestDataService() {}

    public RagIngestData.RagIngestDataResult execute(RagIngestData ragIngestData)  {

        predictedHome = ragIngestData.getPredictedHome();
        predictedAway = ragIngestData.getPredictedAway();

        actualHome = ragIngestData.getActualHome();
        actualAway = ragIngestData.getActualAway();

        homeTeam = ragIngestData.getHomeTeam();
        awayTeam = ragIngestData.getAwayTeam();
        username = ragIngestData.getUsername();
        round = ragIngestData.getRound();

        pointsEarned = ragIngestData.getPointsEarned();
        roundPoints = ragIngestData.getRoundPoints();
        totalPoints = ragIngestData.getTotalPoints();

        phrases = new LinkedHashSet<>();

        predictedOutcome = outcome(predictedHome, predictedAway);

        actualOutcome    = outcome(actualHome, actualAway);
        hitExactScore   = predictedHome == actualHome && predictedAway == actualAway;
        hitOutcome      = predictedOutcome.equals(actualOutcome);

        createUserPhrases();
        createMatchInfoPhrase();
        createScoreInfoPhrase();
        createPointsPlusMatchDetailPhrase();

        return new RagIngestData.RagIngestDataResult(
                String.join(". ", phrases) + ".",
                getStringObjectMap(ragIngestData.getBetDate())
        );
    }

    private Map<String, Object> getStringObjectMap(String date) {
        Map<String, Object> md = new LinkedHashMap<>();
        md.put("type", "bet_join_embedding");
        md.put("username", username);
        md.put("round", round);
        md.put("betDate", date);
        md.put("homeTeam", homeTeam);
        md.put("awayTeam", awayTeam);
        md.put("predictedHome", predictedHome);
        md.put("predictedAway", predictedAway);
        md.put("actualHome", actualHome);
        md.put("actualAway", actualAway);
        md.put("pointsEarned", pointsEarned);
        md.put("roundPoints", roundPoints);
        md.put("totalPoints", totalPoints);
        md.put("predictedOutcome", predictedOutcome);
        md.put("actualOutcome", actualOutcome);
        md.put("hitExactScore", hitExactScore);
        md.put("hitOutcome", hitOutcome);
        md.put("sentenceCount", phrases.size());
        return md;
    }

    private void createUserPhrases() {
        phrases.add(String.format("%s apostou que o jogo entre %s e %s terminaria %d a %d",
                username, homeTeam, awayTeam, predictedHome, predictedAway));

        switch (predictedOutcome) {
            case "HOME" -> {
                phrases.add(String.format("%s apostou vitória do %s por %d a %d sobre o %s",
                        username, homeTeam, predictedHome, predictedAway, awayTeam));
                phrases.add(String.format("Palpite de %s indicava triunfo do %s por %d a %d contra o %s",
                        username, homeTeam, predictedHome, predictedAway, awayTeam));
            }
            case "AWAY" -> {
                phrases.add(String.format("%s apostou vitória do %s por %d a %d sobre o %s",
                        username, awayTeam, predictedAway, predictedHome, homeTeam));
                phrases.add(String.format("Palpite de %s indicava triunfo do %s por %d a %d contra o %s",
                        username, awayTeam, predictedAway, predictedHome, homeTeam));
            }
            default -> { // DRAW
                phrases.add(String.format("%s apostou em empate entre %s e %s em %d a %d",
                        username, homeTeam, awayTeam, predictedHome, predictedAway));
                phrases.add(String.format("Palpite de %s: igualdade entre %s e %s por %d a %d",
                        username, homeTeam, awayTeam, predictedHome, predictedAway));
            }
        }
    }

    private void createMatchInfoPhrase() {
        phrases.add(String.format("O jogo entre %s e %s terminou %d a %d",
                homeTeam, awayTeam, actualHome, actualAway));
        switch (actualOutcome) {
            case "HOME" -> {
                phrases.add(String.format("O %s venceu o %s por %d a %d",
                        homeTeam, awayTeam, actualHome, actualAway));
                phrases.add(String.format("O %s foi vitorioso, enquanto o %s foi derrotado",
                        homeTeam, awayTeam));
            }
            case "AWAY" -> {
                phrases.add(String.format("O %s venceu o %s por %d a %d",
                        awayTeam, homeTeam, actualAway, actualHome));
                phrases.add(String.format("O %s foi vitorioso, enquanto o %s foi derrotado",
                        awayTeam, homeTeam));
            }
            default -> { // DRAW
                if (actualHome == 0 && actualAway == 0) {
                    phrases.add(String.format("Empate sem gols entre %s e %s",
                            homeTeam, awayTeam));
                } else {
                    phrases.add(String.format("Partida terminou empatada em %d a %d entre %s e %s",
                            actualHome, actualAway, homeTeam, awayTeam));
                }
            }
        }
    }

    private void createScoreInfoPhrase() {
        if (hitExactScore) {
            phrases.add(String.format("%s acertou o placar exato (%d a %d)",
                    username, actualHome, actualAway));
        } else {
            phrases.add(String.format("%s não acertou o placar exato (palpite %d a %d, real %d a %d)",
                    username, predictedHome, predictedAway, actualHome, actualAway));
        }

        if (hitOutcome) {
            phrases.add(String.format("%s acertou o vencedor da partida",
                    username));
        } else {
            if (!actualOutcome.equals("DRAW")) {
                phrases.add(String.format("%s errou o vencedor da partida", username));
            } else {
                if (!predictedOutcome.equals("DRAW")) {
                    phrases.add(String.format("%s errou o tipo de resultado (palpite com vencedor, jogo empatado)",
                            username));
                }
            }
        }
    }

    private void createPointsPlusMatchDetailPhrase() {
        if (pointsEarned > 0) {
            phrases.add(String.format("%s fez %d pontos neste jogo", username, pointsEarned));
            phrases.add(String.format("%s marcou %d pontos com este palpite", username, pointsEarned));
        } else {
            phrases.add(String.format("%s não fez pontos neste jogo", username));
        }

        int diff = Math.abs(actualHome - actualAway);
        if (diff >= 3) {
            String winner = actualHome > actualAway ? homeTeam : awayTeam;
            String loser  = actualHome > actualAway ? awayTeam : homeTeam;
            phrases.add(String.format("O %s aplicou uma goleada no %s", winner, loser));
            phrases.add(String.format("Goleada: %s %d x %d %s",
                    winner, Math.max(actualHome, actualAway), Math.min(actualHome, actualAway), loser));
            phrases.add(String.format("O %s sofreu uma goleada do %s", loser, winner));
        }

        // Clean sheet
        if (actualHome > 0 && actualAway == 0) {
            phrases.add(String.format("O %s não tomou gols do %s",
                    homeTeam, awayTeam));
            phrases.add(String.format("O %s não conseguiu marcar contra o %s",
                    awayTeam, homeTeam));
        } else if (actualAway > 0 && actualHome == 0) {
            phrases.add(String.format("O %s não tomou gols do %s",
                    awayTeam, homeTeam));
            phrases.add(String.format("O %s não conseguiu marcar contra o %s",
                    homeTeam, awayTeam));
        }

        phrases.add(String.format("Na rodada %d, %s soma %d pontos e no total tem %d",
                round, username, roundPoints, totalPoints));

        String conj = hitOutcome ? "e" : "mas";
        phrases.add(String.format("%s apostou %d a %d entre %s e %s, %s o jogo terminou %d a %d",
                username, predictedHome, predictedAway, homeTeam, awayTeam, conj, actualHome, actualAway));
        phrases.add(String.format("%s apostou em %s, %s o resultado foi %s",
                username, prettyOutcome(predictedOutcome, homeTeam, awayTeam, predictedHome, predictedAway),
                conj, prettyOutcome(actualOutcome, homeTeam, awayTeam, actualHome, actualAway)));
    }

    private static String outcome(int home, int away) {
        if (home > away) return "HOME";
        if (away > home) return "AWAY";
        return "DRAW";
    }

    private static String prettyOutcome(String outcome, String home, String away, int h, int a) {
        return switch (outcome) {
            case "HOME" -> String.format("vitória do %s por %d a %d", home, h, a);
            case "AWAY" -> String.format("vitória do %s por %d a %d", away, a, h);
            default     -> String.format("empate em %d a %d", h, a);
        };
    }


}
