package br.com.g12.model;

import java.util.Map;

public class RagIngestData {
   private String username;
   private String homeTeam;
   private String awayTeam;
   private int predictedHome;
   private int predictedAway;
   private int actualHome;
   private int actualAway;
   private int pointsEarned;
   private int roundPoints;
   private int totalPoints;
   private int round;
   private String betDate;

   public record RagIngestDataResult(
           String content,
           Map<String, Object> metaData
   ){
   }

   public RagIngestData() {}

   public RagIngestData(String username, String homeTeam, String awayTeam, int predictedHome, int predictedAway, int actualHome, int actualAway, int pointsEarned, int roundPoints, int totalPoints, int round, String date) {
      this.username = username;
      this.homeTeam = homeTeam;
      this.awayTeam = awayTeam;
      this.predictedHome = predictedHome;
      this.predictedAway = predictedAway;
      this.actualHome = actualHome;
      this.actualAway = actualAway;
      this.pointsEarned = pointsEarned;
      this.roundPoints = roundPoints;
      this.totalPoints = totalPoints;
      this.round = round;
      this.betDate = date;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getHomeTeam() {
      return homeTeam;
   }

   public void setHomeTeam(String homeTeam) {
      this.homeTeam = homeTeam;
   }

   public String getAwayTeam() {
      return awayTeam;
   }

   public void setAwayTeam(String awayTeam) {
      this.awayTeam = awayTeam;
   }

   public int getPredictedHome() {
      return predictedHome;
   }

   public void setPredictedHome(int predictedHome) {
      this.predictedHome = predictedHome;
   }

   public int getPredictedAway() {
      return predictedAway;
   }

   public void setPredictedAway(int predictedAway) {
      this.predictedAway = predictedAway;
   }

   public int getActualHome() {
      return actualHome;
   }

   public void setActualHome(int actualHome) {
      this.actualHome = actualHome;
   }

   public int getActualAway() {
      return actualAway;
   }

   public void setActualAway(int actualAway) {
      this.actualAway = actualAway;
   }

   public int getPointsEarned() {
      return pointsEarned;
   }

   public void setPointsEarned(int pointsEarned) {
      this.pointsEarned = pointsEarned;
   }

   public int getRoundPoints() {
      return roundPoints;
   }

   public void setRoundPoints(int roundPoints) {
      this.roundPoints = roundPoints;
   }

   public int getTotalPoints() {
      return totalPoints;
   }

   public void setTotalPoints(int totalPoints) {
      this.totalPoints = totalPoints;
   }

   public int getRound() {
      return round;
   }

   public void setRound(int round) {
      this.round = round;
   }

   public String getBetDate() {
      return betDate;
   }

   public void setBetDate(String betDate) {
      this.betDate = betDate;
   }
}











