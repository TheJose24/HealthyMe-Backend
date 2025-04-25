package dev.diegoqm.healthyme_infraestructura.dto;

import dev.diegoqm.healthyme_infraestructura.enums.DiaSemana;
import lombok.Data;
import java.time.LocalTime;

@Data
public class HorarioTrabajoDTO {

    private Integer id;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
