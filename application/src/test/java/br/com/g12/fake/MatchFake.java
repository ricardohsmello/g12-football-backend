package br.com.g12.fake;

import br.com.g12.model.Match;
import br.com.g12.model.Score;

import java.util.Date;

public class MatchFake {

    public static MatchBuilder builder() {
        return new MatchBuilder();
    }

    public static class MatchBuilder {
        private String id = "1";
        private int round = 5;
        private String homeTeam = "Corinthians";
        private String awayTeam = "Real Madrid";
        private Date matchDate = new Date();
        private Score score;
        private String status;

        public MatchBuilder id(String id) {
            this.id = id;
            return this;
        }

        public MatchBuilder round(int round) {
            this.round = round;
            return this;
        }

        public MatchBuilder homeTeam(String homeTeam) {
            this.homeTeam = homeTeam;
            return this;
        }

        public MatchBuilder awayTeam(String awayTeam) {
            this.awayTeam = awayTeam;
            return this;
        }

        public MatchBuilder withSameTeams(String teamName) {
            this.homeTeam = teamName;
            this.awayTeam = teamName;
            return this;
        }

        public MatchBuilder withNullTeams() {
            this.homeTeam = null;
            this.awayTeam = null;
            return this;
        }

        public MatchBuilder matchDate(Date matchDate) {
            this.matchDate = matchDate;
            return this;
        }

        public MatchBuilder score(Score score) {
            this.score = score;
            return this;
        }

        public MatchBuilder status(String status) {
            this.status = status;
            return this;
        }

        public Match build() {
            return new Match(id, round, homeTeam, awayTeam, matchDate, score, status);
        }
    }
}