package studio.devbyjose.healthyme_commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.payment.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private Integer id;
    private BigDecimal monto;
    private EstadoPago estado;
    private LocalDateTime fechaPago;
    private EntidadOrigen entidadReferencia;
    private Integer idReferencia;
    private Integer idPaciente;
    private MetodoPagoDTO metodoPago;
}