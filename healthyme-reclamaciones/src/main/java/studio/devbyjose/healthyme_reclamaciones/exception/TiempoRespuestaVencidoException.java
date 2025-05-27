package studio.devbyjose.healthyme_reclamaciones.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ResponseStatus(HttpStatus.CONFLICT)
public class TiempoRespuestaVencidoException extends RuntimeException {
    
    private final String numeroReclamacion;
    private final LocalDateTime fechaLimite;
    private final LocalDateTime fechaActual;
    private final long diasVencidos;
    
    public TiempoRespuestaVencidoException(String numeroReclamacion, LocalDateTime fechaLimite, long diasVencidos) {
        super(buildMessage(numeroReclamacion, fechaLimite, diasVencidos));
        this.numeroReclamacion = numeroReclamacion;
        this.fechaLimite = fechaLimite;
        this.fechaActual = LocalDateTime.now();
        this.diasVencidos = diasVencidos;
    }
    
    public TiempoRespuestaVencidoException(String numeroReclamacion, LocalDateTime fechaLimite) {
        this(numeroReclamacion, fechaLimite, calculateDaysOverdue(fechaLimite));
    }
    
    private static String buildMessage(String numeroReclamacion, LocalDateTime fechaLimite, long diasVencidos) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format(
            "La reclamación %s tiene el tiempo de respuesta vencido. " +
            "Fecha límite: %s. Días vencidos: %d",
            numeroReclamacion,
            fechaLimite.format(formatter),
            diasVencidos
        );
    }
    
    private static long calculateDaysOverdue(LocalDateTime fechaLimite) {
        return java.time.temporal.ChronoUnit.DAYS.between(fechaLimite, LocalDateTime.now());
    }
    
    // Getters
    public String getNumeroReclamacion() {
        return numeroReclamacion;
    }
    
    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }
    
    public LocalDateTime getFechaActual() {
        return fechaActual;
    }
    
    public long getDiasVencidos() {
        return diasVencidos;
    }
}
