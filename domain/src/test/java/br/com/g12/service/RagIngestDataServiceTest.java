package br.com.g12.service;

import br.com.g12.model.RagIngestData;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RagIngestDataServiceTest {

    private final RagIngestDataService service = new RagIngestDataService();

    @Test
    public void should_generate_phrases_and_metadata_for_exact_score_hit() {
        RagIngestData d = new RagIngestData();
        d.setUsername("Ricardo");
        d.setHomeTeam("Corinthians");
        d.setAwayTeam("Cruzeiro");
        d.setPredictedHome(2);
        d.setPredictedAway(1);
        d.setActualHome(2);
        d.setActualAway(1);
        d.setPointsEarned(5);
        d.setRoundPoints(10);
        d.setTotalPoints(100);
        d.setRound(1);
        d.setBetDate("2025-08-10T20:00:00Z");

        RagIngestData.RagIngestDataResult resp = service.execute(d);

        assertNotNull(resp);
        assertNotNull(resp.content());
        assertTrue(resp.content().contains("Ricardo"));
        assertTrue(resp.content().contains("Corinthians"));
        assertTrue(resp.content().contains("Cruzeiro"));

        Map<String, Object> md = resp.metaData();
        assertEquals("Ricardo", md.get("username"));
        assertEquals("Corinthians", md.get("homeTeam"));
        assertEquals("Cruzeiro", md.get("awayTeam"));
        assertEquals(2, md.get("predictedHome"));
        assertEquals(1, md.get("predictedAway"));
        assertEquals(2, md.get("actualHome"));
        assertEquals(1, md.get("actualAway"));
        assertEquals(5, md.get("pointsEarned"));
        assertEquals(10, md.get("roundPoints"));
        assertEquals(100, md.get("totalPoints"));
        assertEquals(1, md.get("round"));
        assertEquals("2025-08-10T20:00:00Z", md.get("betDate"));
        assertEquals("bet_join_embedding", md.get("type"));
        assertEquals(true, md.get("hitExactScore"));
        assertEquals(true, md.get("hitOutcome"));
        assertTrue(((Number) md.get("sentenceCount")).intValue() > 0);
    }

    @Test
    public void should_generate_phrases_for_draw_without_exact_score() {
        RagIngestData d = new RagIngestData();
        d.setUsername("Joana");
        d.setHomeTeam("Corinthians");
        d.setAwayTeam("Cruzeiro");
        d.setPredictedHome(1);
        d.setPredictedAway(1);
        d.setActualHome(0);
        d.setActualAway(0);
        d.setPointsEarned(0);
        d.setRoundPoints(5);
        d.setTotalPoints(50);
        d.setRound(2);
        d.setBetDate("2025-08-10T20:00:00Z");

        RagIngestData.RagIngestDataResult resp = service.execute(d);

        assertNotNull(resp.content());
        assertTrue(resp.content().toLowerCase().contains("empate"));

        Map<String, Object> md = resp.metaData();
        assertEquals(false, md.get("hitExactScore"));
        assertEquals(true, md.get("hitOutcome"));
    }

    @Test
    public void should_handle_big_win_as_many_score() {
        RagIngestData d = new RagIngestData();
        d.setUsername("Maria");
        d.setHomeTeam("Corinthians");
        d.setAwayTeam("Cruzeiro");
        d.setPredictedHome(3);
        d.setPredictedAway(0);
        d.setActualHome(5);
        d.setActualAway(1);
        d.setPointsEarned(3);
        d.setRoundPoints(8);
        d.setTotalPoints(80);
        d.setRound(3);
        d.setBetDate("2025-08-10T20:00:00Z");

        RagIngestData.RagIngestDataResult resp = service.execute(d);

        String content = resp.content().toLowerCase();
        assertTrue(content.contains("goleada"));
        assertTrue(resp.content().contains("O Corinthians aplicou uma goleada no Cruzeiro"));

        Map<String, Object> md = resp.metaData();
        assertEquals(true, md.get("hitOutcome"));
        assertEquals(false, md.get("hitExactScore"));
    }
}
