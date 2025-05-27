package dev.Elmer.healthyme_consultas.dto;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.*;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RecetaDto {

    private Integer idReceta;

    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate fechaEmision;

    @NotNull(message = "El ID de la consulta es obligatorio")
    private Integer idConsulta;

    private List<MedicamentoDto> medicamentos;
}