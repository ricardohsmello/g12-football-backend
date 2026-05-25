package br.com.g12.model;

public final class CompetitionDefaults {

    public static final String DEFAULT_COMPETITION_ID = "brasileirao";
    public static final String DEFAULT_STAGE = "LEAGUE";

    private CompetitionDefaults() {
    }

    public static String competitionIdOrDefault(String competitionId) {
        return competitionId == null || competitionId.isBlank() ? DEFAULT_COMPETITION_ID : competitionId;
    }

    public static String stageOrDefault(String stage) {
        return stage == null || stage.isBlank() ? DEFAULT_STAGE : stage;
    }
}
