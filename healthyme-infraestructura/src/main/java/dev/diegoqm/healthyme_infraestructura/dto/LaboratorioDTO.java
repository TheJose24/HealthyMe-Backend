package dev.diegoqm.healthyme_infraestructura.dto;

import dev.diegoqm.healthyme_infraestructura.entity.Sede;
import lombok.Data;

@Data
public class LaboratorioDTO {

    private Integer id;
    private String nombre;
    private Integer piso;
    private Integer numeroHabitacion;
    private Integer idSede;
    private Integer idUnidad;
}
