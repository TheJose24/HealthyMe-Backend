package studio.devbyjose.healthyme_payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.payment.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer id;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo", nullable = false)
    private MetodoPago metodoPago;

    // Pago para citas, consultas, examenes, recetas, etc
    @Enumerated(EnumType.STRING)
    @Column(name = "entidad_referencia", nullable = false)
    private EntidadOrigen entidadReferencia;

    // ID de la entidad de referencia (cita, consulta, examen o receta)
    @Column(name = "id_entididad_referencia", nullable = false)
    private Integer entidadReferenciaId;

    // ID de pago generado por Stripe
    @Column(name = "payment_intent_id", unique = true)
    private String paymentIntentId;

    @Column(name = "id_paciente", nullable = false)
    private Long idPaciente;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;
}