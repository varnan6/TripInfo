package com.tripinfo.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts exceptions into clean JSON error responses so the client
 * always receives a structured body instead of a raw Spring error page.
 *
 * Handled cases:
 * - @Valid failures on POST body → 400 with per-field messages
 * - Unknown routes → 404 with a friendly message
 * - Unexpected runtime errors → 500 (generic, no internal detail leaked)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Bean Validation failure on a @RequestBody (@Valid triggered). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Validation failed");
        body.put("details", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /** Route not found. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "The requested endpoint does not exist."));
    }

    /** Catch-all — never leak stack traces to the client. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        ex.printStackTrace(); // log server-side only
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
    }
}
