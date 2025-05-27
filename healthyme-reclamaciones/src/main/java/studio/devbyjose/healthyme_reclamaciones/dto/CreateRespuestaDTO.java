package studio.devbyjose.healthyme_reclamaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_reclamaciones.enums.TipoRespuesta;

import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRespuestaDTO {
    
    @NotNull(message = "El ID de la reclamación es obligatorio")
    private Long reclamacionId;
    
    @NotNull(message = "El tipo de respuesta es obligatorio")
    private TipoRespuesta tipoRespuesta;
    
    @NotBlank(message = "El contenido de la respuesta es obligatorio")
    @Size(min = 10, max = 5000, message = "El contenido debe tener entre 10 y 5000 caracteres")
    private String contenido;
    
    @NotBlank(message = "El responsable es obligatorio")
    @Size(max = 100, message = "El responsable no puede exceder 100 caracteres")
    private String responsable;
    
    @Builder.Default
    private Boolean esRespuestaFinal = false;
    
    @Size(max = 5000, message = "La solución aplicada no puede exceder 5000 caracteres")
    private String solucionAplicada;
    
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;
    
    @Builder.Default
    private Boolean notificarCliente = true;
}
