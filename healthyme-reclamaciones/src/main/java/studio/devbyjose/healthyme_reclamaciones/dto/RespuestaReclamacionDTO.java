package studio.devbyjose.healthyme_reclamaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_reclamaciones.enums.TipoRespuesta;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaReclamacionDTO {
    
    private Long id;
    private Long reclamacionId;
    private String numeroReclamacion; // Para referencia
    private TipoRespuesta tipoRespuesta;
    private String contenido;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaRespuesta;
    
    private String responsable;
    private Boolean notificadoCliente;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaNotificacion;
    
    private Boolean esRespuestaFinal;
    private String solucionAplicada;
    private String observaciones;
}
