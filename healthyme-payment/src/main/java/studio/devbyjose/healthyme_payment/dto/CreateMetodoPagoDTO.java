package studio.devbyjose.healthyme_payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_commons.enums.payment.TipoMetodoPago;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMetodoPagoDTO {
    @NotNull(message = "El tipo de método de pago es obligatorio")
    private TipoMetodoPago tipo;

    @NotBlank(message = "El nombre del método de pago es obligatorio")
    private String nombre;

    private Boolean estado = true;
}