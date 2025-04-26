package dev.juliancamacho.healthyme_personal.dto;

import dev.juliancamacho.healthyme_personal.enums.DiaSemana;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioTrabajoDto {

    private int idHorario;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
