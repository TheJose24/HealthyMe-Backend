package dev.diegoqm.healthyme_infraestructura.dto;

import dev.diegoqm.healthyme_infraestructura.entity.HorarioTrabajo;
import lombok.Data;

@Data
public class SedeDTO {

    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private HorarioTrabajo horarioTrabajo;
}