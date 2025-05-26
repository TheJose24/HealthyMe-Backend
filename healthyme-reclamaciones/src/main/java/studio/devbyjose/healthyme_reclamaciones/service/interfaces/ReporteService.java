package studio.devbyjose.healthyme_reclamaciones.service.interfaces;

import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Servicio para generación de reportes y estadísticas
 */
public interface ReporteService {

    // =================== ESTADÍSTICAS GENERALES ===================

    /**
     * Obtener estadísticas generales del sistema
     * @return EstadisticasDTO Estadísticas completas
     */
    EstadisticasDTO obtenerEstadisticasGenerales();

    /**
     * Obtener estadísticas por período
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return EstadisticasDTO Estadísticas del período
     */
    EstadisticasDTO obtenerEstadisticasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtener estadísticas por área responsable
     * @param areaResponsable Área responsable
     * @param fechaInicio Fecha de inicio (opcional)
     * @param fechaFin Fecha de fin (opcional)
     * @return EstadisticasDTO Estadísticas del área
     */
    EstadisticasDTO obtenerEstadisticasPorArea(String areaResponsable, 
                                              LocalDateTime fechaInicio, 
                                              LocalDateTime fechaFin);

    // =================== REPORTES DE RECLAMACIONES ===================

    /**
     * Generar reporte de reclamaciones por criterios
     * @param estado Estado de las reclamaciones
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param tipoMotivo Tipo de motivo
     * @param areaResponsable Área responsable
     * @param pageable Parámetros de paginación
     * @return Page<ReclamacionReporteDTO> Página de reporte
     */
    Page<ReclamacionReporteDTO> generarReporteReclamaciones(
            EstadoReclamacion estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            TipoMotivo tipoMotivo,
            String areaResponsable,
            Pageable pageable
    );

    /**
     * Generar reporte de reclamaciones vencidas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param pageable Parámetros de paginación
     * @return Page<ReclamacionReporteDTO> Página de reclamaciones vencidas
     */
    Page<ReclamacionReporteDTO> generarReporteReclamacionesVencidas(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin, 
            Pageable pageable
    );

    /**
     * Generar reporte de tiempo de respuesta
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param areaResponsable Área responsable (opcional)
     * @return List<Map<String, Object>> Lista con métricas de tiempo
     */
    List<Map<String, Object>> generarReporteTiempoRespuesta(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String areaResponsable
    );

    // =================== REPORTES DE TENDENCIAS ===================

    /**
     * Obtener tendencia de reclamaciones por mes
     * @param mesesAtras Cantidad de meses hacia atrás
     * @return Map<String, Long> Mapa con mes y cantidad de reclamaciones
     */
    Map<String, Long> obtenerTendenciaMensual(int mesesAtras);

    /**
     * Obtener tendencia por tipo de motivo
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Map<String, Long> Mapa con tipo de motivo y cantidad
     */
    Map<String, Long> obtenerTendenciaPorTipoMotivo(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtener tendencia por canal de recepción
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Map<String, Long> Mapa con canal y cantidad
     */
    Map<String, Long> obtenerTendenciaPorCanal(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // =================== REPORTES DE RENDIMIENTO ===================

    /**
     * Obtener reporte de rendimiento por usuario
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return List<Map<String, Object>> Lista con métricas por usuario
     */
    List<Map<String, Object>> obtenerReporteRendimientoPorUsuario(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin
    );

    /**
     * Obtener reporte de rendimiento por área
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return List<Map<String, Object>> Lista con métricas por área
     */
    List<Map<String, Object>> obtenerReporteRendimientoPorArea(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin
    );

    /**
     * Calcular tiempo promedio de respuesta
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param areaResponsable Área responsable (opcional)
     * @return Double Tiempo promedio en días
     */
    Double calcularTiempoPromedioRespuesta(LocalDateTime fechaInicio, 
                                          LocalDateTime fechaFin, 
                                          String areaResponsable);

    // =================== EXPORTACIÓN DE DATOS ===================

    /**
     * Exportar reclamaciones a Excel
     * @param criterios Criterios de filtrado
     * @return byte[] Archivo Excel en bytes
     */
    byte[] exportarReclamacionesExcel(Map<String, Object> criterios);

    /**
     * Exportar estadísticas a PDF
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return byte[] Archivo PDF en bytes
     */
    byte[] exportarEstadisticasPDF(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Exportar reporte de rendimiento a CSV
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return byte[] Archivo CSV en bytes
     */
    byte[] exportarReporteRendimientoCSV(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // =================== DASHBOARD Y MÉTRICAS ===================

    /**
     * Obtener datos para dashboard principal
     * @return Map<String, Object> Datos del dashboard
     */
    Map<String, Object> obtenerDatosDashboard();

    /**
     * Obtener métricas en tiempo real
     * @return Map<String, Object> Métricas actuales
     */
    Map<String, Object> obtenerMetricasTiempoReal();

    /**
     * Obtener top reclamaciones por área
     * @param limite Cantidad máxima de resultados
     * @return List<Map<String, Object>> Top áreas con más reclamaciones
     */
    List<Map<String, Object>> obtenerTopAreasPorReclamaciones(int limite);

    /**
     * Obtener alertas de gestión (vencimientos, sobrecargas, etc.)
     * @return List<Map<String, Object>> Lista de alertas
     */
    List<Map<String, Object>> obtenerAlertasGestion();
}
