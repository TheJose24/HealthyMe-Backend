package dev.Elmer.healthyme_consultas.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RecetaDto {
    private Integer idReceta;
    private String medicamento;
    private String dosis;
    private Integer instrucciones;
    private LocalDate fechaEmision;
    private Integer idConsulta;
}