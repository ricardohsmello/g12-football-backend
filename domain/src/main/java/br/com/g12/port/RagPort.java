package br.com.g12.port;

import br.com.g12.model.RagIngestData;

import java.util.List;

public interface RagPort {
    void add(List<RagIngestData.RagIngestDataResult> data);
    String answer(String question);
}
