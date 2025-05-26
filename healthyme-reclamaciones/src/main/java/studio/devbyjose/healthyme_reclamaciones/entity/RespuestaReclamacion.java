package studio.devbyjose.healthyme_reclamaciones.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_reclamaciones.enums.TipoRespuesta;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "respuestas_reclamacion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaReclamacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reclamacion_id", nullable = false)
    private Reclamacion reclamacion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_respuesta", nullable = false)
    private TipoRespuesta tipoRespuesta;
    
    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @CreationTimestamp
    @Column(name = "fecha_respuesta", nullable = false)
    private LocalDateTime fechaRespuesta;
    
    @Column(name = "responsable", nullable = false, length = 100)
    private String responsable;
    
    @Column(name = "notificado_cliente", nullable = false)
    @Builder.Default
    private Boolean notificadoCliente = false;
    
    @Column(name = "fecha_notificacion")
    private LocalDateTime fechaNotificacion;
    
    @Column(name = "es_respuesta_final", nullable = false)
    @Builder.Default
    private Boolean esRespuestaFinal = false;
    
    @Column(name = "solucion_aplicada", columnDefinition = "TEXT")
    private String solucionAplicada;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
