package br.com.g12.database.mongodb.impl;

import br.com.g12.model.RagIngestData;
import br.com.g12.port.RagPort;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RagPortImpl implements RagPort {

    private final VectorStore vectorStore;

    RagPortImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void add(List<RagIngestData.RagIngestDataResult> data) {

        List<Document> documents = new ArrayList<>();

        for (RagIngestData.RagIngestDataResult it : data) {
            documents.add(new Document(it.content(), it.metaData()));
        }

        var splitter = TokenTextSplitter.builder().withChunkSize(800).build();
        var chunks = splitter.apply(documents);

        vectorStore.add(chunks);
    }
}
