package br.com.g12.model;

public record LiveBetProjection(String username, Score prediction, int projectedPoints) {}
