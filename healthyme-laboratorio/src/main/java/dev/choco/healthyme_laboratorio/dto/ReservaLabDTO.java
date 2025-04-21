package dev.choco.healthyme_laboratorio.dto;

import lombok.Data;
import java.time.*;

@Data
public class ReservaLabDTO {
    private Integer idReservaLab;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;
    private Integer idPaciente;
    private Integer idTecnico;
    private Integer idLaboratorio;
}
