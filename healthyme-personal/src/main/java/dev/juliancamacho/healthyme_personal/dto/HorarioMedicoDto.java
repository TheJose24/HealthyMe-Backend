package dev.juliancamacho.healthyme_personal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HorarioMedicoDto
{
    @NotNull(message = "El ID medico no puede ser nulo")
    private Integer idMedico;

    @NotNull(message = "El ID horario no puede ser nulo")
    private Integer idHorario;
}
