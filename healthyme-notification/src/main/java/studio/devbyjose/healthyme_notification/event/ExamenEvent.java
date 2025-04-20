package studio.devbyjose.healthyme_notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamenEvent {
    private Integer idExamen;
    private TipoEventoExamen tipoEvento;
    private Integer idPaciente;
    private String emailPaciente;
    private String tipoExamen;
    private LocalDateTime fecha;
    private List<Map<String, Object>> resultados;
    private String observaciones;

    public enum TipoEventoExamen {
        SOLICITUD_CREADA,
        MUESTRA_TOMADA,
        RESULTADO_DISPONIBLE
    }
}