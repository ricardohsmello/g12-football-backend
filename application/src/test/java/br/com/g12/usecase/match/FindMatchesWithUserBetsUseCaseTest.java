package br.com.g12.usecase.match;

import br.com.g12.exception.FindMatchesWithUserException;
import br.com.g12.model.CompetitionDefaults;
import br.com.g12.model.MatchWithPrediction;
import br.com.g12.model.Score;
import br.com.g12.port.MatchPort;
import br.com.g12.request.UserRoundRequest;
import br.com.g12.response.MatchResponse;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class FindMatchesWithUserBetsUseCaseTest {

    private final MatchPort matchPort = mock(MatchPort.class);
    private final FindMatchesWithUserBetsUseCase useCase = new FindMatchesWithUserBetsUseCase(matchPort);

    @Test
    public void should_create_match_successfully() {
        UserRoundRequest request = new UserRoundRequest("ricas", "ricas", 32, 2025);

        List<MatchWithPrediction> mockResult = List.of(
                matchWithPrediction("CLOSED", new Score(1, 0), 11)
        );

        when(matchPort.findByCompetitionIdAndRoundUserAndYear(CompetitionDefaults.DEFAULT_COMPETITION_ID, "ricas", 32, 2025)).thenReturn(mockResult);

        List<MatchResponse> result = useCase.execute(request);

        verify(matchPort).findByCompetitionIdAndRoundUserAndYear(CompetitionDefaults.DEFAULT_COMPETITION_ID, "ricas", 32, 2025);
        assertEquals(mockResult.size(), result.size());
    }

    @Test
    public void shouldThrowExceptionWhenUsernameIsNull() {
        UserRoundRequest request = new UserRoundRequest(null, "ricas", 10, 2025);

        FindMatchesWithUserException exception = assertThrows(
                FindMatchesWithUserException.class,
                () -> useCase.execute(request)
        );

        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenCurrentUsernameIsNull() {
        UserRoundRequest request = new UserRoundRequest("ricas", null, 10, 2025);

        FindMatchesWithUserException exception = assertThrows(
                FindMatchesWithUserException.class,
                () -> useCase.execute(request)
        );

        assertEquals("Current username is required", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenRoundIsInvalid() {
        UserRoundRequest request = new UserRoundRequest("ricas", "ricas", -1, 2025);

        FindMatchesWithUserException exception = assertThrows(
                FindMatchesWithUserException.class,
                () -> useCase.execute(request)
        );

        assertEquals("Round must be greater than or equal to 0", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenYearIsInvalid() {
        UserRoundRequest request = new UserRoundRequest("ricas", "ricas", 10, 1899);

        FindMatchesWithUserException exception = assertThrows(
                FindMatchesWithUserException.class,
                () -> useCase.execute(request)
        );

        assertEquals("Year must be greater than or equal to 1900", exception.getMessage());
    }

    @Test
    public void shouldHidePredictionWhenMatchIsOpenAndSelectedUserIsNotCurrentUser() {
        UserRoundRequest request = new UserRoundRequest("friend", "ricas", 13, 2025);
        MatchWithPrediction openMatch = matchWithPrediction("OPEN", new Score(2, 1), 0);

        when(matchPort.findByCompetitionIdAndRoundUserAndYear(CompetitionDefaults.DEFAULT_COMPETITION_ID, "friend", 13, 2025)).thenReturn(List.of(openMatch));

        List<MatchResponse> result = useCase.execute(request);

        assertEquals(1, result.size());
        assertEquals(null, result.getFirst().prediction());
        assertEquals(null, result.getFirst().pointsEarned());
    }

    @Test
    public void shouldShowPredictionWhenMatchIsOpenAndSelectedUserIsCurrentUser() {
        UserRoundRequest request = new UserRoundRequest("ricas", "ricas", 13, 2025);
        Score prediction = new Score(2, 1);
        MatchWithPrediction openMatch = matchWithPrediction("OPEN", prediction, 0);

        when(matchPort.findByCompetitionIdAndRoundUserAndYear(CompetitionDefaults.DEFAULT_COMPETITION_ID, "ricas", 13, 2025)).thenReturn(List.of(openMatch));

        List<MatchResponse> result = useCase.execute(request);

        assertEquals(prediction, result.getFirst().prediction());
        assertEquals(0, result.getFirst().pointsEarned());
    }

    @Test
    public void shouldShowPredictionWhenMatchIsClosedForAnotherUser() {
        UserRoundRequest request = new UserRoundRequest("friend", "ricas", 13, 2025);
        Score prediction = new Score(2, 1);
        MatchWithPrediction closedMatch = matchWithPrediction("CLOSED", prediction, 7);

        when(matchPort.findByCompetitionIdAndRoundUserAndYear(CompetitionDefaults.DEFAULT_COMPETITION_ID, "friend", 13, 2025)).thenReturn(List.of(closedMatch));

        List<MatchResponse> result = useCase.execute(request);

        assertEquals(prediction, result.getFirst().prediction());
        assertEquals(7, result.getFirst().pointsEarned());
    }

    private MatchWithPrediction matchWithPrediction(String status, Score prediction, int pointsEarned) {
        MatchWithPrediction match = mock(MatchWithPrediction.class);
        when(match.getId()).thenReturn("match-1");
        when(match.getRound()).thenReturn(13);
        when(match.getHomeTeam()).thenReturn("Corinthians");
        when(match.getAwayTeam()).thenReturn("Palmeiras");
        when(match.getMatchDate()).thenReturn(new Date());
        when(match.getStatus()).thenReturn(status);
        when(match.getPrediction()).thenReturn(prediction);
        when(match.getPointsEarned()).thenReturn(pointsEarned);
        return match;
    }
}
