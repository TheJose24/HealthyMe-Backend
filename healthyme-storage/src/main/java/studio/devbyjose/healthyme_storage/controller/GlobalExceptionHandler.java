package studio.devbyjose.healthyme_storage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import studio.devbyjose.healthyme_commons.dto.ErrorResponseDTO;
import studio.devbyjose.healthyme_commons.exception.HttpStatusProvider;
import studio.devbyjose.healthyme_commons.exception.ResourceNotFoundException;
import studio.devbyjose.healthyme_storage.exception.StorageException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request, ex);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponseDTO> handleStorageException(
            StorageException ex, WebRequest request) {
        log.error("Error de almacenamiento: {}", ex.getMessage(), ex);
        HttpStatus status = getStatusFromException(ex);
        return buildErrorResponse("Error: " + ex.getMessage(), status, request, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllExceptions(
            Exception ex, WebRequest request) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);
        return buildErrorResponse("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR, request, ex);
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(String message, HttpStatus status, WebRequest request, Exception ex) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    private HttpStatus getStatusFromException(RuntimeException ex) {
        if (ex instanceof HttpStatusProvider) {
            return ((HttpStatusProvider) ex).getHttpStatus();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }


}
