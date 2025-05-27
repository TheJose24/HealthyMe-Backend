package studio.devbyjose.healthyme_reclamaciones.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reclamaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reclamacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_reclamacion", nullable = false, unique = true, length = 20)
    private String numeroReclamacion; // REC-2025-000001
    
    @Column(name = "numero_hoja", length = 50)
    private String numeroHoja; // Para cumplimiento legal
    
    @CreationTimestamp
    @Column(name = "fecha_reclamacion", nullable = false)
    private LocalDateTime fechaReclamacion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_motivo", nullable = false)
    private TipoMotivo tipoMotivo;
    
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;
    
    // Datos del reclamante
    @Column(name = "nombre_reclamante", nullable = false, length = 200)
    private String nombreReclamante;
    
    @Column(name = "dni_reclamante", length = 20)
    private String dniReclamante;
    
    @Column(name = "email_reclamante", length = 100)
    private String emailReclamante;
    
    @Column(name = "telefono_reclamante", length = 20)
    private String telefonoReclamante;
    
    @Column(name = "direccion_reclamante", length = 300)
    private String direccionReclamante;
    
    // Datos del incidente
    @Column(name = "servicio_criticado", length = 200)
    private String servicioCriticado;
    
    @Column(name = "fecha_incidente")
    private LocalDateTime fechaIncidente;
    
    @Column(name = "detalle_incidente", columnDefinition = "TEXT")
    private String detalleIncidente;
    
    // Gestión
    @Enumerated(EnumType.STRING)
    @Column(name = "canal_recepcion", nullable = false)
    private CanalRecepcion canalRecepcion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoReclamacion estado = EstadoReclamacion.RECIBIDO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad")
    @Builder.Default
    private PrioridadReclamacion prioridad = PrioridadReclamacion.MEDIA;
    
    @Column(name = "requiere_respuesta", nullable = false)
    @Builder.Default
    private Boolean requiereRespuesta = true;
    
    @Column(name = "fecha_limite_respuesta")
    private LocalDateTime fechaLimiteRespuesta;
    
    // Relaciones
    @Column(name = "id_paciente")
    private Long idPaciente; // Referencia al paciente si está registrado
    
    @Column(name = "id_cita")
    private Long idCita; // Referencia a la cita relacionada
    
    @Column(name = "id_pago")
    private Long idPago; // Referencia al pago relacionado
    
    // Asignación
    @Column(name = "asignado_a")
    private String asignadoA; // Usuario responsable de la gestión
    
    @Column(name = "area_responsable", length = 100)
    private String areaResponsable;
    
    // Auditoria
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    // Relaciones con otras entidades
    @OneToMany(mappedBy = "reclamacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RespuestaReclamacion> respuestas;
    
    @OneToMany(mappedBy = "reclamacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SeguimientoReclamacion> seguimientos;
    
    @OneToMany(mappedBy = "reclamacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdjuntoReclamacion> adjuntos;
    
    // Métodos de utilidad
    @PrePersist
    public void prePersist() {
        if (fechaLimiteRespuesta == null && requiereRespuesta && prioridad != null) {
            fechaLimiteRespuesta = fechaReclamacion.plusDays(prioridad.getDiasRespuesta());
        }
    }
}
