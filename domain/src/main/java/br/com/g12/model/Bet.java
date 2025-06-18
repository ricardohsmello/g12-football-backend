package br.com.g12.model;

import java.util.Date;

import static br.com.g12.utils.DateUtils.truncateToSeconds;

public class Bet {
    private String id;
    private String matchId;
    private String username;
    private Score prediction;
    private int round;
    private Integer pointsEarned;
    private Date date;

    public Bet(String id, String matchId, String username, Score prediction, int round, Integer pointsEarned, Date date) {
        this.id = id;
        this.matchId = matchId;
        this.username = username;
        this.prediction = prediction;
        this.round = round;
        this.pointsEarned = pointsEarned;
        this.date = truncateToSeconds(date);;
    }

    public String getId() { return id; }
    public String getMatchId() { return matchId; }
    public String getUsername() { return username; }
    public Score getPrediction() { return prediction; }
    public int getRound() { return round; }

    public Date getDate() {
        return date;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public void setDate(Date date) {
        this.date = truncateToSeconds(date);
    }
}
