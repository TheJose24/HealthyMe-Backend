package dev.diegoqm.healthyme_citas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.diegoqm.healthyme_citas.enums.EstadoCita;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CitaDTO {
    private String id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    @JsonFormat(pattern = "HH:mm:ss")
    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @NotNull(message = "El estado es obligatorio")
    private EstadoCita estado;

    @NotNull(message = "El id del paciente es obligatorio")
    private Long idPaciente;

    @NotNull(message = "El id del medico es obligatorio")
    private Integer idMedico;
    private String idConsultorio;

}