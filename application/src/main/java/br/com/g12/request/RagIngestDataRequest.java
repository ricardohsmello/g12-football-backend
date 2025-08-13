package br.com.g12.request;

public record RagIngestDataRequest(
        String username,
        String homeTeam,
        String awayTeam,
        int predictedHome,
        int predictedAway,
        int actualHome,
        int actualAway,
        int pointsEarned,
        int roundPoints,
        int totalPoints,
        int round,
        java.util.Date betDate
) {}