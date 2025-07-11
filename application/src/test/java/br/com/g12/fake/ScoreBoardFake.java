package br.com.g12.fake;

import br.com.g12.model.Scoreboard;

import java.util.List;

public class ScoreBoardFake {

    public static Scoreboard getOne() {
        return new Scoreboard("1", 13, "ricardo", 12);
    }

    public static List<Scoreboard> getManyTotal() {
        return List.of(
                new Scoreboard("1", 0, "pedro", 44),
                new Scoreboard("2", 0, "ricardo", 32)
        );

    }
}
