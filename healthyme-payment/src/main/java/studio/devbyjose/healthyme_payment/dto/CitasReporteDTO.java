package studio.devbyjose.healthyme_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_commons.client.dto.CitasPorDiaDTO;
import studio.devbyjose.healthyme_commons.client.dto.CitasPorEspecialidadDTO;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitasReporteDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long totalCitas;
    private Long citasPendientes;
    private Long citasRealizadas;
    private Long citasCanceladas;
    private Double tasaCompletitud; // Porcentaje de citas realizadas
    private List<CitasPorDiaDTO> citasPorDia;
    private List<CitasPorEspecialidadDTO> citasPorEspecialidad;
}