package br.com.g12.model;

import java.util.Date;

public class MatchWithPrediction {
    private String id;
    private String competitionId;
    private String stage;
    private String group;
    private int round;
    private String homeTeam;
    private String awayTeam;
    private Date matchDate;
    private Score score;
    private Score prediction;
    private int pointsEarned;
    private String status;

    public MatchWithPrediction() {}

    public MatchWithPrediction(String id, String competitionId, String stage, String group, int round,
                               String homeTeam, String awayTeam, Date matchDate, Score score,
                               Score prediction, int pointsEarned, String status) {
        this.id = id;
        this.competitionId = competitionId;
        this.stage = stage;
        this.group = group;
        this.round = round;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.score = score;
        this.prediction = prediction;
        this.pointsEarned = pointsEarned;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public String getStage() {
        return stage;
    }

    public String getGroup() {
        return group;
    }

    public int getRound() {
        return round;
    }

    public Date getMatchDate() {
        return matchDate;
    }


    public Score getPrediction() {
        return prediction;
    }

    public Score getScore() {
        return score;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
