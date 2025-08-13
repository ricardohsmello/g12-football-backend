package br.com.g12.usecase.rag;

import br.com.g12.request.RagIngestDataRequest;
import br.com.g12.response.RagIngestDataResponse;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RagIngestDataUseCaseTest {

    private final RagIngestDataUseCase useCase = new RagIngestDataUseCase();

    @Test
    public void should_generate_phrases_and_metadata_for_exact_score_hit() {
        RagIngestDataRequest request = new RagIngestDataRequest(
                "Ricardo",
                "Corinthians",
                "Cruzeiro",
                2,
                1,
                2,
                1,
                5,
                10,
                100,
                1,
                new Date()
        );

        RagIngestDataResponse response = useCase.execute(request);

        assertNotNull(response);
        assertNotNull(response.content());
        assertTrue(response.content().contains("Ricardo"));
        assertTrue(response.content().contains("Corinthians"));
        assertTrue(response.content().contains("Cruzeiro"));

        Map<String, Object> metadata = response.metaData();
        assertEquals("Ricardo", metadata.get("username"));
        assertEquals("Corinthians", metadata.get("homeTeam"));
        assertEquals("Cruzeiro", metadata.get("awayTeam"));
        assertEquals(2, metadata.get("predictedHome"));
        assertEquals(1, metadata.get("predictedAway"));
        assertEquals(2, metadata.get("actualHome"));
        assertEquals(1, metadata.get("actualAway"));
        assertEquals(5, metadata.get("pointsEarned"));
        assertEquals(10, metadata.get("roundPoints"));
        assertEquals(100, metadata.get("totalPoints"));
        assertEquals(true, metadata.get("hitExactScore"));
        assertEquals(true, metadata.get("hitOutcome"));
    }

    @Test
    public void should_generate_phrases_for_draw_without_exact_score() {
        RagIngestDataRequest request = new RagIngestDataRequest(
                "Joana",
                "Corinthians",
                "Cruzeiro",
                1, 1,
                0, 0,
                0,
                5,
                50,
                2,
                new Date()
        );

        RagIngestDataResponse response = useCase.execute(request);

        assertNotNull(response.content());
        assertTrue(response.content().contains("empate"));
        assertFalse((Boolean) response.metaData().get("hitExactScore"));
    }

    @Test
    public void should_handle_big_win_as_many_score() {
        RagIngestDataRequest request = new RagIngestDataRequest(
                "Maria",
                "Corinthians",
                "Cruzeiro",
                3, 0,
                5, 1,
                3,
                8,
                80,
                3,
                new Date()
        );

        RagIngestDataResponse response = useCase.execute(request);
        assertTrue(response.content().contains("goleada"));
        assertTrue(response.content().contains("O Corinthians aplicou uma goleada no Cruzeiro"));
        assertTrue((Boolean) response.metaData().get("hitOutcome"));
        assertFalse((Boolean) response.metaData().get("hitExactScore"));
    }
}
