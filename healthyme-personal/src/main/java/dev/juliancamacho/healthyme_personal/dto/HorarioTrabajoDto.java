package dev.juliancamacho.healthyme_personal.dto;

import dev.juliancamacho.healthyme_personal.entity.HorarioTrabajo;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioTrabajoDto {

    private int idHorario;
    private HorarioTrabajo.DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
