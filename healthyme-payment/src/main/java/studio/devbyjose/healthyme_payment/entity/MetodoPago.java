package studio.devbyjose.healthyme_payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_commons.enums.payment.TipoMetodoPago;

@Entity
@Table(name = "metodo_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoMetodoPago tipo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}