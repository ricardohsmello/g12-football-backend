package br.com.g12.fakedata;

import br.com.g12.entity.MatchDocument;
import br.com.g12.model.Score;

import java.util.Date;

public class MatchFakeData {

    public static MatchDocument createFakeMatch(int round, String home, String away, Date matchDate, Score score, String status) {
        return new MatchDocument(null,  round, home, away, matchDate, score, status);
    }

    public static MatchDocument createFakeOpenMatchForToday() {
        return createFakeMatch(
                13,
                "Botafogo",
                "Corinthians",
                new Date(),
                new Score(2, 2),
                "OPEN"
        );
    }
}
