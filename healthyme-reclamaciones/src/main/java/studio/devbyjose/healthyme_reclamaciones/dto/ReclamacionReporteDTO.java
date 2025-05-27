package studio.devbyjose.healthyme_reclamaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReclamacionReporteDTO {
    
    private String numeroReclamacion;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime fechaReclamacion;
    
    private String tipoMotivo;
    private String tipoReclamacion;
    private String nombreReclamante;
    private String canalRecepcion;
    private String estado;
    private String prioridad;
    private String areaResponsable;
    private String asignadoA;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime fechaLimiteRespuesta;
    
    private Boolean esVencida;
    private Integer diasVencidos;
    private Integer totalRespuestas;
    private String descripcionResumida; // Primeros 100 caracteres
}
