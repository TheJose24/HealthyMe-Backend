package studio.devbyjose.healthyme_reclamaciones.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_reclamaciones.enums.EstadoReclamacion;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento_reclamaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoReclamacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reclamacion_id", nullable = false)
    private Reclamacion reclamacion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoReclamacion estadoAnterior;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false)
    private EstadoReclamacion estadoNuevo;
    
    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;
    
    @Column(name = "accion_realizada", length = 200)
    private String accionRealizada;
    
    @CreationTimestamp
    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;
    
    @Column(name = "usuario_responsable", length = 100)
    private String usuarioResponsable;
    
    @Column(name = "es_visible_cliente", nullable = false)
    @Builder.Default
    private Boolean esVisibleCliente = true;
}
