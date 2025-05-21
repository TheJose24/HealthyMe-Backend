package studio.devbyjose.healthyme_notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaEvent {
    private Integer idCita;
    private TipoEventoCita tipoEvento;
    private Long idPaciente;
    private String emailPaciente;
    private Integer idMedico;
    private String especialidad;
    private LocalDate fecha;
    private LocalTime hora;
    private String consultorio;
    private String motivoCambio;

    public enum TipoEventoCita {
        CREACION,
        ACTUALIZACION,
        CANCELACION,
        RECORDATORIO
    }
}