package br.com.g12.entity;

import br.com.g12.model.Match;
import br.com.g12.model.Score;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "match")
@CompoundIndexes({
        @CompoundIndex(name = "statusAndDate", def = "{'status': 1, 'matchDate': 1}"),
        @CompoundIndex(name = "competitionRoundAndStatus", def = "{'competitionId': 1, 'round': 1, 'status': 1}"),
        @CompoundIndex(name = "competitionRoundStatusAndDate", def = "{'competitionId': 1, 'round': 1, 'status': 1, 'matchDate': 1}")
})

public class MatchDocument {
    private String id;
    @Indexed(name = "competitionId_1")
    private String competitionId;
    private String stage;
    private String group;
    @Indexed(name = "round_1")
    private int round;
    private String homeTeam;
    private String awayTeam;
    private Date matchDate;
    private Score score;
    private String status;

    public MatchDocument() {}


    public Date getMatchDate() {
        return matchDate;
    }

    public MatchDocument(String id, String competitionId, String stage, String group, int round, String homeTeam, String awayTeam, Date matchDate, Score score, String status) {
        this.id = id;
        this.competitionId = competitionId;
        this.stage = stage;
        this.group = group;
        this.round = round;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.score = score;
        this.status = status;
    }

    public MatchDocument(String id, int round, String homeTeam, String awayTeam, Date matchDate, Score score, String status) {
        this(id, null, null, null, round, homeTeam, awayTeam, matchDate, score, status);
    }

    public static MatchDocument fromModel(Match match) {
        return new MatchDocument(
                match.getId(),
                match.getCompetitionId(),
                match.getStage(),
                match.getGroup(),
                match.getRound(),
                match.getHomeTeam(),
                match.getAwayTeam(),
                match.getMatchDate(),
                match.getScore(),
                match.getStatus()
        );
    }

    public int getRound() {
        return round;
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public Match toModel() {
        return new Match(id, competitionId, stage, group, round, homeTeam, awayTeam, matchDate, score, status);
    }
}
