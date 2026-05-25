package br.com.g12.request;

public record UserRoundRequest(
        String username,
        String currentUsername,
        String competitionId,
        int round,
        int year
) {
    public UserRoundRequest(String username, String currentUsername, int round, int year) {
        this(username, currentUsername, null, round, year);
    }
}
