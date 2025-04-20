package studio.devbyjose.healthyme_notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaEvent {
    private Integer idConsulta;
    private Integer idCita;
    private Integer idPaciente;
    private String emailPaciente;
    private Integer idMedico;
    private LocalDateTime fechaConsulta;
    private String diagnostico;
    private String observaciones;
    private Boolean requiereExamenes;
}