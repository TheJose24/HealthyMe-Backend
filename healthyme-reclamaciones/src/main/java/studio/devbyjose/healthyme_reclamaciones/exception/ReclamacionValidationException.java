package studio.devbyjose.healthyme_reclamaciones.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.HashMap;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReclamacionValidationException extends RuntimeException {
    
    private final Map<String, String> errors;
    private final String errorCode;
    
    public ReclamacionValidationException(String message) {
        super(message);
        this.errors = new HashMap<>();
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public ReclamacionValidationException(String message, String errorCode) {
        super(message);
        this.errors = new HashMap<>();
        this.errorCode = errorCode;
    }
    
    public ReclamacionValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors != null ? new HashMap<>(errors) : new HashMap<>();
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public ReclamacionValidationException(String message, String field, String error) {
        super(message);
        this.errors = new HashMap<>();
        this.errors.put(field, error);
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public ReclamacionValidationException(String message, String errorCode, Map<String, String> errors) {
        super(message);
        this.errors = errors != null ? new HashMap<>(errors) : new HashMap<>();
        this.errorCode = errorCode;
    }
    
    public Map<String, String> getErrors() {
        return new HashMap<>(errors);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void addError(String field, String error) {
        this.errors.put(field, error);
    }
    
    // Métodos estáticos para casos comunes
    public static ReclamacionValidationException campoRequerido(String field) {
        return new ReclamacionValidationException(
            "Campo requerido: " + field, 
            "REQUIRED_FIELD", 
            Map.of(field, "Este campo es obligatorio")
        );
    }
    
    public static ReclamacionValidationException formatoInvalido(String field, String formato) {
        return new ReclamacionValidationException(
            "Formato inválido en campo: " + field, 
            "INVALID_FORMAT", 
            Map.of(field, "Formato esperado: " + formato)
        );
    }
    
    public static ReclamacionValidationException valorDuplicado(String field, String value) {
        return new ReclamacionValidationException(
            "Valor duplicado: " + value, 
            "DUPLICATE_VALUE", 
            Map.of(field, "Ya existe una reclamación con este " + field)
        );
    }
    
    public static ReclamacionValidationException estadoInvalido(String estadoActual, String estadoNuevo) {
        return new ReclamacionValidationException(
            "Cambio de estado inválido", 
            "INVALID_STATE_TRANSITION", 
            Map.of("estado", "No se puede cambiar de " + estadoActual + " a " + estadoNuevo)
        );
    }
}
