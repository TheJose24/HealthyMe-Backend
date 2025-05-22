package dev.juliancamacho.healthyme_personal.dto;

import dev.juliancamacho.healthyme_personal.enums.DiaSemana;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioTrabajoDto {

    private Integer idHorario;

    @NotNull(message = "El dia de la semana no puede ser nulo")
    private DiaSemana diaSemana;

    @NotNull(message = "La hora de inicio no puede ser nulo")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin no puede ser nulo")
    private LocalTime horaFin;
}
