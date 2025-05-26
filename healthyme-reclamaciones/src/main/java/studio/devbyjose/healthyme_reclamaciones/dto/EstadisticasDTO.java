package studio.devbyjose.healthyme_reclamaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDTO {
    
    // Contadores principales
    private Long totalReclamaciones;
    private Long totalRecibidas;
    private Long totalEnProceso;
    private Long totalResueltas;
    private Long totalVencidas;
    
    // Por tipo de motivo
    private Map<String, Long> reclamacionesPorTipoMotivo;
    
    // Por canal de recepción
    private Map<String, Long> reclamacionesPorCanal;
    
    // Por estado
    private Map<String, Long> reclamacionesPorEstado;
    
    // Por prioridad
    private Map<String, Long> reclamacionesPorArea;
    
    // Tiempos promedio
    private Double tiempoPromedioRespuesta; // En días
    private Double tiempoPromedioResolucion; // En días
    
    // Porcentajes
    private Double porcentajeRespuestasATiempo;
    private Double porcentajeResolucionesATiempo;
    
    // Tendencias (últimos 30 días)
    private Map<String, Long> tendenciaUltimos30Dias;
    
    // Top áreas con más reclamaciones
    private Map<String, Long> topAreasConMasReclamaciones;
    
    // Satisfacción del cliente (si aplica)
    private Double puntuacionPromedioCerradas;
}
