package dev.juliancamacho.healthyme_personal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HorarioTecnicoDto
{
    @NotNull(message = "El ID tecnico no puede ser nulo")
    private Integer idTecnico;

    @NotNull(message = "El ID horario no puede ser nulo")
    private Integer idHorario;
}
