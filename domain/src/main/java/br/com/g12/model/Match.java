package br.com.g12.model;

import java.util.Date;

import static br.com.g12.utils.DateUtils.truncateToSeconds;

public class Match {
    String id;
    int round;
    String homeTeam;
    String awayTeam;
    Date matchDate;
    Score score;
    String status;

    public Match(String id, int round, String homeTeam, String awayTeam, Date matchDate, Score score, String status) {
        this.id = id;
        this.round = round;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = truncateToSeconds(matchDate);
        this.score = score;
        this.status = status;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = truncateToSeconds(matchDate);
    }

    public String getId() {return id; }

    public int getRound() { return round; }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public Date getMatchDate() { return matchDate; }

    public Score getScore() { return score; }

    public void setScore(Score score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

