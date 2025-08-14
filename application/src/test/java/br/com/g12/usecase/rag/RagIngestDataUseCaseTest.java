package br.com.g12.usecase.rag;

import br.com.g12.model.RagIngestData;
import br.com.g12.port.RagPort;
import br.com.g12.service.RagIngestDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RagIngestDataUseCaseTest {

    private RagIngestDataService ragIngestDataService;
    private RagPort ragPort;
    private RagIngestDataUseCase useCase;

    @BeforeEach
    void setUp() {
        ragIngestDataService = mock(RagIngestDataService.class);
        ragPort = mock(RagPort.class);
        useCase = new RagIngestDataUseCase(ragIngestDataService, ragPort);
    }

    @Test
    void generateDocs_shouldParseCsvAndCallDependencies() throws Exception {
        String csvData = """
                username,homeTeam,awayTeam,prediction.homeTeam,prediction.awayTeam,actualHome,actualAway,pointsEarned,roundPoints,totalPoints,round,date
                joao,Corinthians,Cruzeiro,2,1,2,1,5,10,100,1,2025-08-10T20:00:00Z
                maria,Palmeiras,Santos,1,1,0,0,0,3,50,2,2025-08-11T20:00:00Z
                """;

        when(ragIngestDataService.execute(any(RagIngestData.class)))
                .thenAnswer(inv -> new RagIngestData.RagIngestDataResult("mock-content", null));

        try (InputStreamReader reader = new InputStreamReader(
                new java.io.ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8)) {

            int count = useCase.generateDocs(reader);

            assertEquals(2, count);
        }

        verify(ragIngestDataService, times(2)).execute(any(RagIngestData.class));

        ArgumentCaptor<List<RagIngestData.RagIngestDataResult>> captor = ArgumentCaptor.forClass(List.class);
        verify(ragPort, times(1)).add(captor.capture());

        List<RagIngestData.RagIngestDataResult> addedDocs = captor.getValue();
        assertEquals(2, addedDocs.size());
        assertEquals("mock-content", addedDocs.getFirst().content());
    }
}
