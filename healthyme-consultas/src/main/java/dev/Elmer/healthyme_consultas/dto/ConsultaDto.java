package dev.Elmer.healthyme_consultas.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ConsultaDto {
    private Integer idConsulta;
    private String sintomas;
    private String diagnostico;
    private LocalDate fecha;
    private Integer idCita;
    private Integer idPaciente;
    private Integer idMedico;

}