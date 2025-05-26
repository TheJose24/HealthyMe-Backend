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
public class CreateReclamacionDTO {
    
    @NotNull(message = "El tipo de motivo es obligatorio")
    private TipoMotivo tipoMotivo;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 5000, message = "La descripción debe tener entre 10 y 5000 caracteres")
    private String descripcion;
    
    // Datos del reclamante
    @NotBlank(message = "El nombre del reclamante es obligatorio")
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
    @NotNull(message = "El canal de recepción es obligatorio")
    private CanalRecepcion canalRecepcion;
    
    private PrioridadReclamacion prioridad;
    
    @Builder.Default
    private Boolean requiereRespuesta = true;
    
    // Relaciones opcionales
    private Long idPaciente;
    private Long idCita;
    private Long idPago;
    
    // Asignación (establecida por el sistema)
    private String areaResponsable;
}
