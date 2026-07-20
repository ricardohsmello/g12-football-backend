package br.com.g12.model;

import java.util.List;

public record Scoreboard (
     String id,
     String competitionId,
     int round,
     String username,
     int points,
     int year,
     List<String> prediction){

    public Scoreboard {
        competitionId = CompetitionDefaults.competitionIdOrDefault(competitionId);
        prediction = prediction == null ? List.of() : List.copyOf(prediction);
    }

    public Scoreboard(String id, String competitionId, int round, String username, int points, int year) {
        this(id, competitionId, round, username, points, year, List.of());
    }

    public Scoreboard(String id, int round, String username, int points, int year) {
        this(id, CompetitionDefaults.DEFAULT_COMPETITION_ID, round, username, points, year, List.of());
    }

    public Scoreboard withPrediction(List<String> prediction) {
        return new Scoreboard(id, competitionId, round, username, points, year, prediction);
    }
}
