package dev.diegoqm.healthyme_infraestructura.controller;

import dev.diegoqm.healthyme_infraestructura.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
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

    @ExceptionHandler({
            ConsultorioNotFoundException.class,
            HorarioTrabajoNotFoundException.class,
            LaboratorioNotFoundException.class,
            SedeNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RuntimeException ex, WebRequest request) {
        log.error("Not Found error: {}", ex.getMessage(), ex);
        HttpStatus status = extractStatus(ex);
        return createErrorResponse(ex.getMessage(), status, request);
    }

    // Maneja errores de validación de argumentos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.error("Validation errors: {}", ex.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            validationErrors.put(field, message);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Error");
        body.put("details", validationErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Captura cualquier otra excepción no manejada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralErrors(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return createErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    private ResponseEntity<ErrorResponseDTO> createErrorResponse(String message, HttpStatus status, WebRequest request) {
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
