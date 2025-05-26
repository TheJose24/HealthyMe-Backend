package studio.devbyjose.healthyme_reclamaciones.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TipoReclamacionException extends RuntimeException {
    
    private final String errorCode;
    
    public TipoReclamacionException(String message) {
        super(message);
        this.errorCode = "TIPO_RECLAMACION_ERROR";
    }
    
    public TipoReclamacionException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    // Métodos estáticos para casos específicos
    public static TipoReclamacionException nombreDuplicado(String nombre) {
        return new TipoReclamacionException(
            "Ya existe un tipo de reclamación con el nombre: " + nombre,
            "NOMBRE_DUPLICADO"
        );
    }
    
    public static TipoReclamacionException tipoInactivo(String nombre) {
        return new TipoReclamacionException(
            "El tipo de reclamación '" + nombre + "' está inactivo",
            "TIPO_INACTIVO"
        );
    }
    
    public static TipoReclamacionException tipoEnUso(Long id) {
        return new TipoReclamacionException(
            "No se puede eliminar el tipo de reclamación porque está siendo usado",
            "TIPO_EN_USO"
        );
    }
}