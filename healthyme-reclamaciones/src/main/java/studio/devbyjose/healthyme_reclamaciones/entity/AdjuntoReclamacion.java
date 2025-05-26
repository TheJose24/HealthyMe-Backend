package studio.devbyjose.healthyme_reclamaciones.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "adjuntos_reclamacion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjuntoReclamacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reclamacion_id", nullable = false)
    private Reclamacion reclamacion;
    
    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;
    
    @Column(name = "nombre_original", nullable = false, length = 255)
    private String nombreOriginal;
    
    @Column(name = "tipo_contenido", length = 100)
    private String tipoContenido;
    
    @Column(name = "tamano_archivo")
    private Long tamanoArchivo;
    
    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;
    
    @Column(name = "descripcion", length = 300)
    private String descripcion;
    
    @CreationTimestamp
    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;
    
    @Column(name = "subido_por", nullable = false, length = 100)
    private String subidoPor;
    
    @Column(name = "es_evidencia", nullable = false)
    @Builder.Default
    private Boolean esEvidencia = false;
}
