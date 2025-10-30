package br.com.g12.database.mongodb.impl;

import br.com.g12.model.RagIngestData;
import br.com.g12.port.RagPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RagPortImpl implements RagPort {
    @Override
    public void add(List<RagIngestData.RagIngestDataResult> data) {

    }

    @Override
    public String answer(String question) {
        return "";
    }

//    private static final int TOP_K = 8;
//    private final VectorStore vectorStore;
//    private final ChatClient chat;
//
//    RagPortImpl(VectorStore vectorStore, ChatClient chat) {
//        this.vectorStore = vectorStore;
//        this.chat = chat;
//    }
//
//    @Override
//    public void add(List<RagIngestData.RagIngestDataResult> data) {
//
//        List<Document> documents = new ArrayList<>();
//
//        for (RagIngestData.RagIngestDataResult it : data) {
//            documents.add(new Document(it.content(), it.metaData()));
//        }
//
//        var splitter = TokenTextSplitter.builder().withChunkSize(800).build();
//        var chunks = splitter.apply(documents);
//
//        vectorStore.add(chunks);
//    }
//
//    @Override
//    public String answer(String question) {
//        SearchRequest.Builder builder = SearchRequest.builder()
//                .query(question)
//                .topK(TOP_K);
//
//        var req = builder.build();
//        List<Document> docs = vectorStore.similaritySearch(req);
//
//        String prompt = "Pergunta: " + question + "\n\nCONTEXTO:\n" + docs.stream()
//                .map(d -> "###\n" + d.getFormattedContent())
//                .collect(Collectors.joining("\n\n"));
//
//        return chat
//                .prompt()
//                .system("""
//                        Você é um assistente especializado em dados de futebol e pontuações de jogadores.
//                    REGRAS:
//                1. Responda SOMENTE usando as informações fornecidas no CONTEXTO abaixo.
//                2. Se a resposta puder ser determinada, informe de forma direta e precisa.
//                3. Se houver números no contexto (por exemplo: pointsEarned, roundPoints, totalPoints), use-os exatamente como estão.
//                4. Se não houver informação suficiente, responda exatamente: "Não encontrei informações suficientes no contexto fornecido para responder."
//                5. NÃO invente ou assuma informações que não estejam no contexto.
//                6. Seja objetivo: máximo de 2 frases.
//                FORMATO DE RESPOSTA:
//                - Apenas o conteúdo da resposta. Não inclua frases introdutórias como "Com base no contexto".
//            """)
//                .user(prompt)
//                .call()
//                .content();
//    }
}
