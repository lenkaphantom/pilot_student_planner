package com.pilot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ---- Validacione greske (@Valid) ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            fieldErrors.put(field, error.getDefaultMessage());
        });
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validacija nije prosla.",
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    // ---- Los email ili lozinka ----
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(simple(HttpStatus.UNAUTHORIZED, "Pogresni kredencijali."));
    }

    // ---- Email vec postoji ----
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(simple(HttpStatus.CONFLICT, ex.getMessage()));
    }

    // ---- Nevazeci token ----
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(simple(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    // ---- Resurs nije pronadjen ----
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(simple(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // ---- Zabranjen pristup ----
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(simple(HttpStatus.FORBIDDEN, "Pristup zabranjen."));
    }

    // ---- Generalna greska ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Neocekivana greska: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(simple(HttpStatus.INTERNAL_SERVER_ERROR, "Doslo je do greske. Pokusajte ponovo."));
    }

    private ErrorResponse simple(HttpStatus status, String message) {
        return new ErrorResponse(status.value(), message, null);
    }

    // ---- DTO za greske ----
    public record ErrorResponse(
            int status,
            String message,
            Map<String, String> fieldErrors
    ) {
        public ErrorResponse {
            // timestamp se moze dodati ako treba
        }
    }
}
