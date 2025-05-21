package studio.devbyjose.healthyme_payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransaccionDTO {
    @NotNull(message = "El ID del pago es obligatorio")
    private Integer idPago;

    private String referenciaExterna;

    private Map<String, Object> datosTransaccion;
}