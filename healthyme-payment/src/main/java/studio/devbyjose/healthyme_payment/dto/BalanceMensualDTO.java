package studio.devbyjose.healthyme_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceMensualDTO {
    private String mes; // Ej: "Enero"
    private BigDecimal ingresos;
    private BigDecimal egresos;
}
