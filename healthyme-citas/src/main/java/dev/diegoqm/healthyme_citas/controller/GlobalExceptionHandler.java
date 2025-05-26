package dev.diegoqm.healthyme_citas.controller;

import dev.diegoqm.healthyme_citas.exception.BadRequestException;
import dev.diegoqm.healthyme_citas.exception.CitaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import studio.devbyjose.healthyme_commons.dto.ErrorResponseDTO;
import studio.devbyjose.healthyme_commons.exception.HttpStatusProvider;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(CitaNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCitaNotFoundException(
            CitaNotFoundException ex, WebRequest request) {
        log.error("Cita no encontrada: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex.getMessage(), ex.getHttpStatus(), request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequestException(
            RuntimeException ex, WebRequest request) {
        log.error("Bad request exception: {}", ex.getMessage());
        HttpStatus status = extractStatus(ex);
        return buildErrorResponse(ex.getMessage(), status, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation errors: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Error");
        response.put("details", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);
        return buildErrorResponse("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(String message, HttpStatus status, WebRequest request) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    private HttpStatus extractStatus(RuntimeException ex) {
        if (ex instanceof HttpStatusProvider) {
            return ((HttpStatusProvider) ex).getHttpStatus();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}