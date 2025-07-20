package studio.devbyjose.healthyme_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_commons.client.dto.CitasPorDiaDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMensualDTO {
    private LocalDate mes;
    private Long citasDelMes;
    private BigDecimal ingresosDelMes;
    private Integer medicosActivosDelMes;
    private List<CitasPorDiaDTO> citasPorDia;
    private List<IngresosPorDiaDTO> ingresosPorDia;
}
