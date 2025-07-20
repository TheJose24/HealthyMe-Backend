package studio.devbyjose.healthyme_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_commons.client.dto.EspecialidadContadaDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteGeneralDTO {
    private LocalDateTime fechaGeneracion;
    private Long totalCitas;
    private Long citasPendientes;
    private Long citasRealizadas;
    private BigDecimal ingresosTotales;
    private BigDecimal ingresosMesActual;
    private Long medicosActivos;
    private Long pacientesTotales;
    private List<EspecialidadContadaDTO> especialidadesMasSolicitadas;
}
