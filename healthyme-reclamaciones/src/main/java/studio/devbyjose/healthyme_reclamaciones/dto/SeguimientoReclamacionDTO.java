package studio.devbyjose.healthyme_reclamaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_reclamaciones.enums.EstadoReclamacion;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoReclamacionDTO {
    
    private Long id;
    private Long reclamacionId;
    private String numeroReclamacion; // Para referencia
    private EstadoReclamacion estadoAnterior;
    private EstadoReclamacion estadoNuevo;
    private String comentario;
    private String accionRealizada;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCambio;
    
    private String usuarioResponsable;
    private Boolean esVisibleCliente;
}