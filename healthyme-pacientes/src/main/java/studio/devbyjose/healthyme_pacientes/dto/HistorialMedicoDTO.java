package studio.devbyjose.healthyme_pacientes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialMedicoDTO {
    private Long id;
    
    @NotNull(message = "El ID de paciente es obligatorio")
    private Long idPaciente;
    
    private Long idTriaje;
    
    private Long idConsulta;
}