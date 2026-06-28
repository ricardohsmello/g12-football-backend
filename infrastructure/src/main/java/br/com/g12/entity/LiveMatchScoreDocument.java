package br.com.g12.entity;

import br.com.g12.model.LiveMatchScore;
import br.com.g12.model.Score;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "live_scores")
public class LiveMatchScoreDocument {

    @Id
    private String id;

    @Indexed(name = "matchId_1", unique = true)
    private ObjectId matchId;

    private String competitionId;
    private Score score;

    @Indexed(name = "createdAt_ttl", expireAfterSeconds = 7200)
    private Date createdAt;

    private Date updatedAt;

    public LiveMatchScoreDocument() {}

    public LiveMatchScoreDocument(String id, String matchId, String competitionId, Score score, Date createdAt, Date updatedAt) {
        this.id = id;
        this.matchId = new ObjectId(matchId);
        this.competitionId = competitionId;
        this.score = score;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LiveMatchScoreDocument fromModel(LiveMatchScore model) {
        return new LiveMatchScoreDocument(
                model.getId(),
                model.getMatchId(),
                model.getCompetitionId(),
                model.getScore(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }

    public LiveMatchScore toModel() {
        return new LiveMatchScore(id, matchId.toString(), competitionId, score, createdAt, updatedAt);
    }

    public String getId() { return id; }
    public ObjectId getMatchId() { return matchId; }
    public String getCompetitionId() { return competitionId; }
    public Score getScore() { return score; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
}
