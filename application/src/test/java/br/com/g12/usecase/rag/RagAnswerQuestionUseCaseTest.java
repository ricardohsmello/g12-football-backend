package br.com.g12.usecase.rag;

import br.com.g12.port.RagPort;
import br.com.g12.request.RagRequest;
import br.com.g12.response.RagAnswer;
import br.com.g12.exception.RagException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RagAnswerQuestionUseCaseTest {

    private RagPort ragPort;
    private RagAnswerQuestionUseCase useCase;

    @BeforeEach
    void setUp() {
        ragPort = mock(RagPort.class);
        useCase = new RagAnswerQuestionUseCase(ragPort);
    }

    @Test
    void should_return_answer_when_port_succeeds() {
        String question = "Quem venceu o clássico?";
        String portAnswer = "Corinthians venceu por 2 a 1.";
        when(ragPort.answer(question)).thenReturn(portAnswer);

        RagRequest request = new RagRequest(question);

        RagAnswer result = useCase.execute(request);

        assertNotNull(result);
        assertEquals(question, result.question());
        assertEquals(portAnswer, result.answer());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ragPort, times(1)).answer(captor.capture());
        assertEquals(question, captor.getValue());

        verifyNoMoreInteractions(ragPort);
    }

    @Test
    void should_propagate_exception_when_port_throws() {
        String question = "Pergunta que causa erro";
        RagRequest request = new RagRequest(question);
        RagException ex = new RagException("Falha no RAG");
        when(ragPort.answer(question)).thenThrow(ex);

        RagException thrown = assertThrows(RagException.class, () -> useCase.execute(request));
        assertSame(ex, thrown);

        verify(ragPort, times(1)).answer(question);
        verifyNoMoreInteractions(ragPort);
    }
}
