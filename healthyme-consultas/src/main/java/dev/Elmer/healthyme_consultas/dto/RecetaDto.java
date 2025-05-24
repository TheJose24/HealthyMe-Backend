package dev.Elmer.healthyme_consultas.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RecetaDto {

    private Integer idReceta;

    @NotBlank(message = "El medicamento no puede estar vacío")
    @Size(max = 100, message = "El nombre del medicamento no debe superar los 100 caracteres")
    private String medicamento;

    @NotBlank(message = "La dosis no puede estar vacía")
    @Size(max = 100, message = "La dosis no debe superar los 100 caracteres")
    private String dosis;

    @NotNull(message = "Las instrucciones son obligatorias")
    @Min(value = 1, message = "Las instrucciones deben ser un número positivo")
    private Integer instrucciones;

    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate fechaEmision;

    @NotNull(message = "El ID de la consulta es obligatorio")
    private Integer idConsulta;
}