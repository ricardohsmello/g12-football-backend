package br.com.g12.model;

public record Scoreboard (
     String id,
     String competitionId,
     int round,
     String username,
     int points,
     int year){

    public Scoreboard {
        competitionId = CompetitionDefaults.competitionIdOrDefault(competitionId);
    }

    public Scoreboard(String id, int round, String username, int points, int year) {
        this(id, CompetitionDefaults.DEFAULT_COMPETITION_ID, round, username, points, year);
    }
}
