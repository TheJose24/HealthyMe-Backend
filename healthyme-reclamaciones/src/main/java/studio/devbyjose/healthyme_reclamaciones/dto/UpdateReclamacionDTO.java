package studio.devbyjose.healthyme_reclamaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReclamacionDTO {
    
    private TipoMotivo tipoMotivo;
    private Long tipoReclamacionId;
    
    @Size(min = 10, max = 5000, message = "La descripción debe tener entre 10 y 5000 caracteres")
    private String descripcion;
    
    // Datos del reclamante
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombreReclamante;
    
    @Pattern(regexp = "^[0-9]{8}$|^[0-9]{11}$", message = "DNI debe tener 8 dígitos o RUC 11 dígitos")
    private String dniReclamante;
    
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String emailReclamante;
    
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Teléfono con formato inválido")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefonoReclamante;
    
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    private String direccionReclamante;
    
    // Datos del incidente
    @Size(max = 200, message = "El servicio criticado no puede exceder 200 caracteres")
    private String servicioCriticado;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaIncidente;
    
    @Size(max = 5000, message = "El detalle del incidente no puede exceder 5000 caracteres")
    private String detalleIncidente;
    
    // Gestión
    private EstadoReclamacion estado;
    private PrioridadReclamacion prioridad;
    private Boolean requiereRespuesta;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaLimiteRespuesta;
    
    // Asignación
    private String asignadoA;
    private String areaResponsable;
    
    // Comentario de actualización
    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String comentarioActualizacion;
}
