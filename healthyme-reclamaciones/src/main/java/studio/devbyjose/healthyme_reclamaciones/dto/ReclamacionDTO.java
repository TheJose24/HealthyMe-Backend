package studio.devbyjose.healthyme_reclamaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReclamacionDTO {
    
    private Long id;
    private String numeroReclamacion;
    private String numeroHoja;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaReclamacion;
    
    private TipoMotivo tipoMotivo;
    private String descripcion;
    
    // Datos del reclamante
    private String nombreReclamante;
    private String dniReclamante;
    private String emailReclamante;
    private String telefonoReclamante;
    private String direccionReclamante;
    
    // Datos del incidente
    private String servicioCriticado;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaIncidente;
    
    private String detalleIncidente;
    
    // Gestión
    private CanalRecepcion canalRecepcion;
    private EstadoReclamacion estado;
    private PrioridadReclamacion prioridad;
    private Boolean requiereRespuesta;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaLimiteRespuesta;
    
    // Relaciones
    private Long idPaciente;
    private Long idCita;
    private Long idPago;
    
    // Asignación
    private String asignadoA;
    private String areaResponsable;
    
    // Auditoria
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private String createdBy;
    private String updatedBy;
    
    // Relaciones
    private List<RespuestaReclamacionDTO> respuestas;
    private List<SeguimientoReclamacionDTO> seguimientos;
    private List<AdjuntoReclamacionDTO> adjuntos;
    
    // Campos calculados
    private Boolean esVencida;
    private Long diasParaVencer;
    private Integer totalRespuestas;
    private Integer totalAdjuntos;
}
