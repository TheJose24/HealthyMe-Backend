package studio.devbyjose.healthyme_notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.notification.EstadoNotificacion;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    @Column(name = "destinatario", length = 100, nullable = false)
    private String destinatario;

    @CreationTimestamp
    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoNotificacion estado;

    // cada notificación está asociada a una única plantilla
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plantilla", nullable = false)
    private Plantilla plantilla;

    @Column(name = "datos_contexto", nullable = false, columnDefinition = "JSON")
    private String datosContexto;

    @Enumerated(EnumType.STRING)
    @Column(name = "entidad_origen", nullable = false)
    private EntidadOrigen entidadOrigen;

    @Column(name = "id_origen", nullable = false)
    private Integer idOrigen;
}
