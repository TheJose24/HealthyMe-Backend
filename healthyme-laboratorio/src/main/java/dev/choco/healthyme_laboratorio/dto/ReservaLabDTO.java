package dev.choco.healthyme_laboratorio.dto;

import dev.choco.healthyme_laboratorio.enums.EstadoReserva;
import lombok.Data;
import java.time.*;

@Data
public class ReservaLabDTO {
    private Integer idReservaLab;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoReserva estado;
    private Integer idPaciente;
    private Integer idTecnico;
    private Integer idLaboratorio;
}
