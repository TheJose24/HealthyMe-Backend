package studio.devbyjose.healthyme_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {
    private Integer id;
    private Integer idPago;
    private LocalDateTime fechaTransaccion;
    private String referenciaExterna;
    private Map<String, Object> datosTransaccion;
}