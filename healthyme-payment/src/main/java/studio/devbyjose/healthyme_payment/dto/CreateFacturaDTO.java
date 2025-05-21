package studio.devbyjose.healthyme_payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFacturaDTO {
    @NotNull(message = "El subtotal es obligatorio")
    @Positive(message = "El subtotal debe ser mayor a cero")
    private BigDecimal subtotal;

    @NotNull(message = "El monto de impuestos es obligatorio")
    @Positive(message = "El monto de impuestos debe ser mayor o igual a cero")
    private BigDecimal impuestos;

    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser mayor a cero")
    private BigDecimal total;

    @NotNull(message = "El ID del pago es obligatorio")
    private Integer idPago;
}