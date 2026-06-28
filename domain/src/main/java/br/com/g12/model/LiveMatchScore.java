package br.com.g12.model;

import java.util.Date;

public class LiveMatchScore {
    private String id;
    private String matchId;
    private String competitionId;
    private Score score;
    private Date updatedAt;

    public LiveMatchScore(String id, String matchId, String competitionId, Score score, Date updatedAt) {
        this.id = id;
        this.matchId = matchId;
        this.competitionId = competitionId;
        this.score = score;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getMatchId() { return matchId; }
    public String getCompetitionId() { return competitionId; }
    public Score getScore() { return score; }
    public Date getUpdatedAt() { return updatedAt; }
}
