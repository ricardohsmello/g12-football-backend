package br.com.g12.entity;

import br.com.g12.model.Bet;
import br.com.g12.model.Score;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "bet")
public class BetDocument {
    @Id
    private String id;
    private ObjectId matchId;
    private String username;
    private Score prediction;
    private int round;
    private Integer pointsEarned;
    private Date date;

    public BetDocument() {}

    public BetDocument(String id, String matchId, String username, Score prediction, int round, Integer pointsEarned, Date date) {
        this.id = id;
        this.matchId = new ObjectId(matchId);
        this.username = username;
        this.prediction = prediction;
        this.round = round;
        this.pointsEarned = pointsEarned;
        this.date = date;
    }

    public static BetDocument fromModel(Bet bet) {
        return new BetDocument(
                bet.getId() != null ? bet.getId() : null,
                bet.getMatchId() != null ? bet.getMatchId() : null,
                bet.getUsername(),
                bet.getPrediction(),
                bet.getRound(),
                bet.getPointsEarned() != null ? bet.getPointsEarned() : null,
                bet.getDate() != null ? bet.getDate() : null
        );
    }

    public void setPrediction(Score prediction) {
        this.prediction = prediction;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Bet toModel() {
        return new Bet(id, matchId.toString(), username, prediction, round, pointsEarned, date);
    }
}