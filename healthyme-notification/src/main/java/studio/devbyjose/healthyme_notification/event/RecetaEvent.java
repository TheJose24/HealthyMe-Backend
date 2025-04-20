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
public class RecetaEvent {
    private Integer idReceta;
    private Integer idConsulta;
    private Integer idPaciente;
    private String emailPaciente;
    private Integer idMedico;
    private LocalDateTime fechaEmision;
    private String indicaciones;
    private List<Map<String, String>> medicamentos;
}