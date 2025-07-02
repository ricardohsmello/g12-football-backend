package br.com.g12.handler;

import br.com.g12.exception.MatchException;
import br.com.g12.exception.ScoreException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MatchException.class)
    public ResponseEntity<ErrorResponse> handleMatchException(MatchException ex) {
        ErrorResponse error = new ErrorResponse("INVALID_MATCH", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ScoreException.class)
    public ResponseEntity<ErrorResponse> handleRoundScore(Exception ex) {
        ErrorResponse error = new ErrorResponse("ROUND_SCORE_ALREADY_EXECUTED", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    public record ErrorResponse(String code, String message) {}
}
