package br.com.g12.usecase.rag;

import br.com.g12.exception.RagException;
import br.com.g12.port.RagPort;
import br.com.g12.request.RagRequest;
import br.com.g12.response.RagAnswer;
import br.com.g12.usecase.AbstractUseCase;

public class RagAnswerQuestionUseCase extends AbstractUseCase<RagRequest> {

    private final RagPort ragPort;

    public RagAnswerQuestionUseCase(RagPort ragPort) {
        this.ragPort = ragPort;
    }

    public RagAnswer execute(RagRequest ragRequest) {
        logInput(ragRequest);
        try {
            return new RagAnswer(
                    ragRequest.question(),
                    ragPort.answer(ragRequest.question())
            );
        } catch (RagException e) {
            logError(e);
            throw e;
        } finally {
            logSuccess();
        }
    }
}
