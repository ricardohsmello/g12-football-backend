package br.com.g12.usecase.rag;

import br.com.g12.model.RagIngestData;
import br.com.g12.port.RagPort;
import br.com.g12.request.RagIngestDataRequest;
import br.com.g12.service.RagIngestDataService;
import br.com.g12.usecase.AbstractUseCase;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class RagIngestDataUseCase extends AbstractUseCase<RagIngestDataRequest> {

    private final RagIngestDataService ragIngestDataService;
    private final RagPort ragPort;

    public RagIngestDataUseCase(RagIngestDataService ragIngestDataService, RagPort ragPort) {
        this.ragIngestDataService = ragIngestDataService;
        this.ragPort = ragPort;
    }

    public int generateDocs(
            InputStreamReader inputStreamReader
    ) throws IOException {

        List<RagIngestData.RagIngestDataResult> docs = new ArrayList<>();
        try (var br = new BufferedReader(inputStreamReader);
             var parser = CSVParser.parse(br, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            for (CSVRecord r : parser) {

                RagIngestData ragIngestData = new RagIngestData(
                        get(r, "username"),
                        get(r, "homeTeam"),
                        get(r, "awayTeam"),
                        toInt(get(r, "prediction.homeTeam")),
                        toInt(get(r, "prediction.awayTeam")),
                        toInt(get(r, "actualHome")),
                        toInt(get(r, "actualAway")),
                        toInt(get(r, "pointsEarned")),
                        toInt(get(r, "roundPoints")),
                        toInt(get(r, "totalPoints")),
                        toInt(get(r, "round")),
                        get(r, "date")
                );

                docs.add(ragIngestDataService.execute(ragIngestData));
            }

            ragPort.add(docs);

        } catch (IOException e) {
            logError(e);
            throw e;
        }

        return docs.size();
    }

    private String get(CSVRecord r, String columnName) {
        String value = r.isMapped(columnName) ? r.get(columnName) : null;
        return value != null ? value.trim() : "";
    }

    private int toInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

}
