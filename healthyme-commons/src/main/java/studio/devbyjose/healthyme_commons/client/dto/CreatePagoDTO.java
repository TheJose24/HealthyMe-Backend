package studio.devbyjose.healthyme_commons.client.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePagoDTO {
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @NotNull(message = "El método de pago es obligatorio")
    private Integer idMetodoPago;

    @NotNull(message = "La entidad de referencia es obligatoria")
    private EntidadOrigen entidadReferencia;

    @NotNull(message = "El ID de referencia es obligatorio")
    private Integer idReferencia;

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long idPaciente;
}