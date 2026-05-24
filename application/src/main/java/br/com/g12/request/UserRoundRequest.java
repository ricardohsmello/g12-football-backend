package br.com.g12.request;

public record UserRoundRequest(
        String username,
        String currentUsername,
        int round,
        int year
) {
}
