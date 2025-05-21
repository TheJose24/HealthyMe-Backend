package studio.devbyjose.healthyme_payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "transaccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion")
    private Integer id;

    @Column(name = "referencia_externa", length = 100)
    private String referenciaExterna;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_transaccion", columnDefinition = "json")
    private Map<String, Object> datosTransaccion;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDateTime fechaTransaccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;
}