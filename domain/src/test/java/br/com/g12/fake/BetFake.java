package br.com.g12.fake;

import br.com.g12.model.Bet;
import br.com.g12.model.Score;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BetFake {

    public static BetBuilder builder() {
        return new BetBuilder();
    }

    public static class BetBuilder {
        private String id;
        private String matchId;
        private String username;
        private Score prediction;
        private int round;
        private Integer pointsEarned;
        private Date date;

        public BetBuilder id(String id) {
            this.id = id;
            return this;
        }
        public BetBuilder matchId(String matchId) {
            this.matchId = matchId;
            return this;
        }
        public BetBuilder username(String username) {
            this.username = username;
            return this;
        }
        public BetBuilder prediction(Score prediction) {
            this.prediction = prediction;
            return this;
        }
        public BetBuilder round(int round) {
            this.round = round;
            return this;
        }

        public BetBuilder date(Date date) {
            this.date = date;
            return this;
        }
        public Bet build() {
            return new Bet(id, matchId, username, prediction, round, pointsEarned, date);
        }

    }
    public static class BetListBuilder {
        private final List<Bet> bets = new ArrayList<>();
        private final String matchId;

        public BetListBuilder(String matchId) {
            this.matchId = matchId;
        }

        public BetListBuilder add(String username, Score prediction) {
            bets.add(new Bet(UUID.randomUUID().toString(), matchId, username, prediction, 1, null, new Date()));
            return this;
        }

        public List<Bet> build() {
            return bets;
        }
    }
}


