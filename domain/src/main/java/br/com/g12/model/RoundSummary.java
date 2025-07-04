package br.com.g12.model;

public record RoundSummary(
        String currentRound,
        int matchesInRound,
        String status,
        TopScore topScore) {

    record TopScore(String name, int score) {}
}

