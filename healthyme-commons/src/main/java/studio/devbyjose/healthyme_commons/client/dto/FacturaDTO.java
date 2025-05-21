package studio.devbyjose.healthyme_commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private Integer id;
    private String numeroFactura;
    private LocalDateTime fechaEmision;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private Integer idPago;
}