package dev.diegoqm.healthyme_citas.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import dev.diegoqm.healthyme_citas.enums.EstadoCita;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CitaDTO {
    private Integer id;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @NotNull(message = "El estado es obligatorio")
    private EstadoCita estado;

    @NotNull(message = "El id del paciente es obligatorio")
    private Integer idPaciente;

    @NotNull(message = "El id del medico es obligatorio")
    private Integer idMedico;
    private Integer idConsultorio;

}