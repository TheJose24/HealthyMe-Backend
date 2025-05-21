package studio.devbyjose.healthyme_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_commons.client.dto.FacturaDTO;
import studio.devbyjose.healthyme_commons.client.dto.MetodoPagoDTO;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.payment.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Integer id;
    private BigDecimal monto;
    private EstadoPago estado;
    private LocalDateTime fechaPago;
    private EntidadOrigen entidadReferencia;
    private Integer idReferencia;
    private Integer idPaciente;
    private MetodoPagoDTO metodoPago;
    private FacturaDTO factura;
    private String paymentIntentId;
    private List<TransaccionDTO> transacciones;
}