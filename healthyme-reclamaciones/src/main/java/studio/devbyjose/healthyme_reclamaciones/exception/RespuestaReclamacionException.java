package studio.devbyjose.healthyme_reclamaciones.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RespuestaReclamacionException extends RuntimeException {
    
    private final String errorCode;
    
    public RespuestaReclamacionException(String message) {
        super(message);
        this.errorCode = "RESPUESTA_ERROR";
    }
    
    public RespuestaReclamacionException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public RespuestaReclamacionException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "RESPUESTA_ERROR";
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    // Métodos estáticos para casos específicos
    public static RespuestaReclamacionException reclamacionYaCerrada(String numeroReclamacion) {
        return new RespuestaReclamacionException(
            "No se puede responder a la reclamación " + numeroReclamacion + " porque ya está cerrada",
            "RECLAMACION_CERRADA"
        );
    }
    
    public static RespuestaReclamacionException respuestaFinalYaExiste(String numeroReclamacion) {
        return new RespuestaReclamacionException(
            "La reclamación " + numeroReclamacion + " ya tiene una respuesta final",
            "RESPUESTA_FINAL_EXISTS"
        );
    }
    
    public static RespuestaReclamacionException notificacionFallida(String numeroReclamacion, String motivo) {
        return new RespuestaReclamacionException(
            "Error al notificar respuesta de la reclamación " + numeroReclamacion + ": " + motivo,
            "NOTIFICATION_FAILED"
        );
    }
}