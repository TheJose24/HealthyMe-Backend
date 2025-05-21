package studio.devbyjose.healthyme_commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_commons.enums.payment.TipoMetodoPago;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoDTO {
    private Integer id;
    private TipoMetodoPago tipo;
    private String nombre;
    private Boolean estado;
}