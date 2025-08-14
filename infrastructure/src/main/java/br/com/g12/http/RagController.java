package br.com.g12.http;

import br.com.g12.request.RagRequest;
import br.com.g12.response.RagAnswer;
import br.com.g12.response.RagIngestDataResponse;
import br.com.g12.usecase.rag.RagAnswerQuestionUseCase;
import br.com.g12.usecase.rag.RagIngestDataUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/rag-admin")
public class RagController {

    private final RagIngestDataUseCase ragIngestDataUseCase;
    private final RagAnswerQuestionUseCase ragAnswerQuestionUseCase;

    RagController(RagIngestDataUseCase ragIngestDataUseCase,
                  RagAnswerQuestionUseCase ragAnswerQuestionUseCase) {
        this.ragIngestDataUseCase = ragIngestDataUseCase;
        this.ragAnswerQuestionUseCase = ragAnswerQuestionUseCase;
    }

    @PostMapping(value = "/ingest-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping("/ask")
    public ResponseEntity<RagAnswer> askGet(
            @RequestBody RagRequest ragRequest
    ) {
        return ResponseEntity.ok(ragAnswerQuestionUseCase.execute(ragRequest));
    }
}
