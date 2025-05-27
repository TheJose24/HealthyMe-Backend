package studio.devbyjose.healthyme_reclamaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjuntoReclamacionDTO {
    
    private Long id;
    private Long reclamacionId;
    private String numeroReclamacion; // Para referencia
    private String nombreArchivo;
    private String nombreOriginal;
    private String tipoContenido;
    private Long tamanoArchivo;
    private String rutaArchivo;
    private String descripcion;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaSubida;
    
    private String subidoPor;
    private Boolean esEvidencia;
    
    // Campos calculados
    private String tamanoFormateado;
    private String urlDescarga;
}