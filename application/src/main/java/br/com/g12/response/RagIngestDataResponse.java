package br.com.g12.response;

import java.util.Map;

public record RagIngestDataResponse(
        String content,
        Map<String, Object> metaData
) {
}
