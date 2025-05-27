package studio.devbyjose.healthyme_reclamaciones.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import studio.devbyjose.healthyme_commons.dto.ErrorResponseDTO;
import studio.devbyjose.healthyme_commons.exception.ResourceNotFoundException;
import studio.devbyjose.healthyme_reclamaciones.exception.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // =================== EXCEPCIONES ESPECÍFICAS DE RECLAMACIONES ===================

    @ExceptionHandler(ReclamacionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleReclamacionNotFound(
            ReclamacionNotFoundException ex, WebRequest request) {
        
        log.warn("Reclamación no encontrada: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.parse(LocalDateTime.now().format(TIMESTAMP_FORMATTER)))
                .status(HttpStatus.NOT_FOUND.value())
                .error("Reclamación No Encontrada")
                .message(ex.getMessage())
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReclamacionValidationException.class)
    public ResponseEntity<Map<String, Object>> handleReclamacionValidation(
            ReclamacionValidationException ex, WebRequest request) {
        
        log.warn("Error de validación en reclamación: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Error de Validación");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("errorCode", ex.getErrorCode());
        errorResponse.put("path", getPath(request));
        
        if (!ex.getErrors().isEmpty()) {
            errorResponse.put("fieldErrors", ex.getErrors());
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TiempoRespuestaVencidoException.class)
    public ResponseEntity<Map<String, Object>> handleTiempoRespuestaVencido(
            TiempoRespuestaVencidoException ex, WebRequest request) {
        
        log.warn("Tiempo de respuesta vencido: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        errorResponse.put("status", HttpStatus.CONFLICT.value());
        errorResponse.put("error", "Tiempo de Respuesta Vencido");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("numeroReclamacion", ex.getNumeroReclamacion());
        errorResponse.put("fechaLimite", ex.getFechaLimite().format(TIMESTAMP_FORMATTER));
        errorResponse.put("diasVencidos", ex.getDiasVencidos());
        errorResponse.put("path", getPath(request));

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RespuestaReclamacionException.class)
    public ResponseEntity<Map<String, Object>> handleRespuestaReclamacion(
            RespuestaReclamacionException ex, WebRequest request) {
        
        log.warn("Error en respuesta de reclamación: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Error en Respuesta de Reclamación");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("errorCode", ex.getErrorCode());
        errorResponse.put("path", getPath(request));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TipoReclamacionException.class)
    public ResponseEntity<Map<String, Object>> handleTipoReclamacion(
            TipoReclamacionException ex, WebRequest request) {
        
        log.warn("Error en tipo de reclamación: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Error en Tipo de Reclamación");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("errorCode", ex.getErrorCode());
        errorResponse.put("path", getPath(request));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AdjuntoReclamacionException.class)
    public ResponseEntity<Map<String, Object>> handleAdjuntoReclamacion(
            AdjuntoReclamacionException ex, WebRequest request) {
        
        log.warn("Error en adjunto de reclamación: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Error en Adjunto de Reclamación");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("errorCode", ex.getErrorCode());
        errorResponse.put("path", getPath(request));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // =================== EXCEPCIONES COMUNES ===================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.parse(LocalDateTime.now().format(TIMESTAMP_FORMATTER)))
                .status(HttpStatus.NOT_FOUND.value())
                .error("Recurso No Encontrado")
                .message(ex.getMessage())
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.warn("Error de validación de argumentos: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing
                ));

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Error de Validación");
        errorResponse.put("message", "Datos de entrada inválidos");
        errorResponse.put("fieldErrors", fieldErrors);
        errorResponse.put("path", getPath(request));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        log.warn("Error de violación de restricciones: {}", ex.getMessage());
        
        Map<String, String> violations = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    ConstraintViolation::getMessage
                ));

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Violación de Restricciones");
        errorResponse.put("message", "Violación de restricciones de validación");
        errorResponse.put("violations", violations);
        errorResponse.put("path", getPath(request));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {
        
        log.error("Error de integridad de datos: {}", ex.getMessage());
        
        String message = "Error de integridad de datos";
        if (ex.getMessage().contains("Duplicate entry")) {
            message = "Ya existe un registro con los datos proporcionados";
        } else if (ex.getMessage().contains("foreign key constraint")) {
            message = "No se puede procesar debido a referencias relacionadas";
        }

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.parse(LocalDateTime.now().format(TIMESTAMP_FORMATTER)))
                .status(HttpStatus.CONFLICT.value())
                .error("Error de Integridad")
                .message(message)
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        log.warn("Error de tipo de argumento: {}", ex.getMessage());
        
        String message = String.format(
            "El parámetro '%s' debe ser de tipo %s",
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido"
        );

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.parse(LocalDateTime.now().format(TIMESTAMP_FORMATTER)))
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Tipo de Parámetro Inválido")
                .message(message)
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        log.warn("Error al leer mensaje HTTP: {}", ex.getMessage());
        
        String message = "Formato de datos inválido en el cuerpo de la petición";
        if (ex.getMessage().contains("JSON")) {
            message = "Error en el formato JSON de la petición";
        }

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.parse(LocalDateTime.now().format(TIMESTAMP_FORMATTER)))
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Formato de Datos Inválido")
                .message(message)
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex, WebRequest request) {
        
        log.warn("Tamaño de archivo excedido: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.parse(LocalDateTime.now().format(TIMESTAMP_FORMATTER)))
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .error("Archivo Muy Grande")
                .message("El archivo excede el tamaño máximo permitido")
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("Argumento ilegal: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.parse(LocalDateTime.now().format(TIMESTAMP_FORMATTER)))
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Argumento Inválido")
                .message(ex.getMessage())
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // =================== EXCEPCIÓN GENERAL ===================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, WebRequest request) {
        
        log.error("Error interno del servidor: ", ex);
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.parse(LocalDateTime.now().format(TIMESTAMP_FORMATTER)))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error Interno del Servidor")
                .message("Ha ocurrido un error inesperado. Por favor, contacte al administrador.")
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // =================== MÉTODOS AUXILIARES ===================

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    /**
     * Método para registrar métricas de errores (opcional)
     */
    private void logMetrics(String exceptionType, String path) {
        // Aquí podrías integrar con un sistema de métricas como Micrometer
        log.info("Error metric - Type: {}, Path: {}", exceptionType, path);
    }
}
