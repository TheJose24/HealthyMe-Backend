package dev.juliancamacho.healthyme_personal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String ERRORS = "errors";

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(
                        error.getField(),
                        Objects.requireNonNullElse(error.getDefaultMessage(), "Error de validación")
                )
        );
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Error de validación en los datos de entrada", errors);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status,
            String message,
            Map<String, String> validationErrors
    ) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put(TIMESTAMP, Instant.now());
        errorResponse.put(STATUS, status.value());
        errorResponse.put(ERROR, status.getReasonPhrase());
        errorResponse.put(MESSAGE, message);

        if (validationErrors != null && !validationErrors.isEmpty()) {
            errorResponse.put(ERRORS, validationErrors);
        }

        return new ResponseEntity<>(errorResponse, status);
    }
}
