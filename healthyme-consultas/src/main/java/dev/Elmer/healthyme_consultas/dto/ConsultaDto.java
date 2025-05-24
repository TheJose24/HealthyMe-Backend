package dev.Elmer.healthyme_consultas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ConsultaDto {

    private Integer idConsulta;

    @NotBlank(message = "Los síntomas no pueden estar vacíos")
    @Size(max = 255, message = "Los síntomas no deben superar los 255 caracteres")
    private String sintomas;

    @NotBlank(message = "El diagnóstico no puede estar vacío")
    @Size(max = 255, message = "El diagnóstico no debe superar los 255 caracteres")
    private String diagnostico;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El ID de la cita es obligatorio")
    private Integer idCita;

    @NotNull(message = "El ID del paciente es obligatorio")
    private Integer idPaciente;

    @NotNull(message = "El ID del médico es obligatorio")
    private Integer idMedico;
}