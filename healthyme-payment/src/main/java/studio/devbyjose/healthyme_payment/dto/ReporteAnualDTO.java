package studio.devbyjose.healthyme_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAnualDTO {
    private Integer year;
    private Long citasDelAno;
    private BigDecimal ingresosDelAno;
    private List<ReporteMensualDTO> reportesPorMes;
}
