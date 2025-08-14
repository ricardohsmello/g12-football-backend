package br.com.g12.http;

import br.com.g12.response.RagIngestDataResponse;
import br.com.g12.usecase.rag.RagIngestDataUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/rag-admin/ingest-data")
public class RagController {

    private final RagIngestDataUseCase ragIngestDataUseCase;

    RagController(RagIngestDataUseCase ragIngestDataUseCase) {
        this.ragIngestDataUseCase = ragIngestDataUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RagIngestDataResponse> ingestData(
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(new RagIngestDataResponse(
                ragIngestDataUseCase.generateDocs(
                        new InputStreamReader(
                                file.getInputStream(),
                                StandardCharsets.UTF_8)
                )));
    }
}
