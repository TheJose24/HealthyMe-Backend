package dev.choco.healthyme_laboratorio.dto;

import dev.choco.healthyme_laboratorio.enums.EstadoReserva;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.*;

@Data
public class ReservaLabDTO {
    private Integer idReservaLab;

    @NotNull(message = "La fecha no puede ser nula")
    private LocalDate fecha;

    @NotNull(message = "La hora no puede ser nula")
    private LocalTime hora;

    @NotNull(message = "El estado no puede ser nulo")
    private EstadoReserva estado;

    @NotNull(message = "El id del paciente no puede ser nulo")
    private Integer idPaciente;

    @NotNull(message = "El id del técnico no puede ser nulo")
    private Integer idTecnico;

    @NotNull(message = "El id del laboratorio no puede ser nulo")
    private Integer idLaboratorio;
}
