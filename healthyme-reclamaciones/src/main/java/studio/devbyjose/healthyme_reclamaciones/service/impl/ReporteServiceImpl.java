package studio.devbyjose.healthyme_reclamaciones.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.entity.*;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import studio.devbyjose.healthyme_reclamaciones.repository.*;
import studio.devbyjose.healthyme_reclamaciones.service.interfaces.ReporteService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de reportes y estadísticas
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private final ReclamacionRepository reclamacionRepository;
    private final RespuestaReclamacionRepository respuestaRepository;
    private final SeguimientoReclamacionRepository seguimientoRepository;

    // =================== ESTADÍSTICAS GENERALES ===================

    @Override
    public EstadisticasDTO obtenerEstadisticasGenerales() {
        log.info("Obteniendo estadísticas generales del sistema");

        try {
            EstadisticasDTO estadisticas = EstadisticasDTO.builder()
                    .totalReclamaciones(reclamacionRepository.count())
                    .totalRecibidas(contarPorEstado(EstadoReclamacion.RECIBIDO))
                    .totalEnProceso(contarPorEstado(EstadoReclamacion.EN_PROCESO))
                    .totalResueltas(contarPorEstado(EstadoReclamacion.RESUELTO))
                    .totalVencidas(calcularTotalVencidas())
                    .reclamacionesPorTipoMotivo(obtenerContadoresPorTipoMotivo())
                    .reclamacionesPorCanal(obtenerContadoresPorCanal())
                    .reclamacionesPorEstado(obtenerContadoresPorEstado())
                    .reclamacionesPorArea(obtenerContadoresPorArea())
                    .tiempoPromedioRespuesta(calcularTiempoPromedioRespuestaGlobal())
                    .tiempoPromedioResolucion(calcularTiempoPromedioResolucionGlobal())
                    .porcentajeRespuestasATiempo(calcularPorcentajeRespuestasATiempo())
                    .porcentajeResolucionesATiempo(calcularPorcentajeResolucionesATiempo())
                    .tendenciaUltimos30Dias(obtenerTendenciaUltimos30Dias())
                    .topAreasConMasReclamaciones(obtenerTopAreasReclamaciones(10))
                    .puntuacionPromedioCerradas(calcularPuntuacionPromedioCerradas())
                    .build();

            log.info("Estadísticas generales obtenidas exitosamente");
            return estadisticas;

        } catch (Exception e) {
            log.error("Error al obtener estadísticas generales: {}", e.getMessage(), e);
            return crearEstadisticasVacias();
        }
    }

    @Override
    public EstadisticasDTO obtenerEstadisticasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo estadísticas para período: {} - {}", fechaInicio, fechaFin);

        try {
            List<Reclamacion> reclamacionesPeriodo = reclamacionRepository
                    .findByFechaReclamacionBetween(fechaInicio, fechaFin);

            EstadisticasDTO estadisticas = EstadisticasDTO.builder()
                    .totalReclamaciones((long) reclamacionesPeriodo.size())
                    .totalRecibidas(contarPorEstadoPeriodo(reclamacionesPeriodo, EstadoReclamacion.RECIBIDO))
                    .totalEnProceso(contarPorEstadoPeriodo(reclamacionesPeriodo, EstadoReclamacion.EN_PROCESO))
                    .totalResueltas(contarPorEstadoPeriodo(reclamacionesPeriodo, EstadoReclamacion.RESUELTO))
                    .totalVencidas(calcularVencidasPeriodo(reclamacionesPeriodo))
                    .reclamacionesPorTipoMotivo(obtenerContadoresPorTipoMotivoPeriodo(reclamacionesPeriodo))
                    .reclamacionesPorCanal(obtenerContadoresPorCanalPeriodo(reclamacionesPeriodo))
                    .reclamacionesPorEstado(obtenerContadoresPorEstadoPeriodo(reclamacionesPeriodo))
                    .reclamacionesPorArea(obtenerContadoresPorAreaPeriodo(reclamacionesPeriodo))
                    .tiempoPromedioRespuesta(calcularTiempoPromedioRespuestaPeriodo(reclamacionesPeriodo))
                    .tiempoPromedioResolucion(calcularTiempoPromedioResolucionPeriodo(reclamacionesPeriodo))
                    .porcentajeRespuestasATiempo(calcularPorcentajeRespuestasATiempoPeriodo(reclamacionesPeriodo))
                    .porcentajeResolucionesATiempo(calcularPorcentajeResolucionesATiempoPeriodo(reclamacionesPeriodo))
                    .build();

            log.info("Estadísticas del período obtenidas exitosamente");
            return estadisticas;

        } catch (Exception e) {
            log.error("Error al obtener estadísticas por período: {}", e.getMessage(), e);
            return crearEstadisticasVacias();
        }
    }

    @Override
    public EstadisticasDTO obtenerEstadisticasPorArea(String areaResponsable, 
                                                     LocalDateTime fechaInicio, 
                                                     LocalDateTime fechaFin) {
        log.info("Obteniendo estadísticas para área: {}", areaResponsable);

        try {
            List<Reclamacion> reclamacionesArea;
            
            if (fechaInicio != null && fechaFin != null) {
                reclamacionesArea = reclamacionRepository
                        .findByAreaResponsableAndFechaReclamacionBetween(areaResponsable, fechaInicio, fechaFin);
            } else {
                reclamacionesArea = reclamacionRepository.findByAreaResponsable(areaResponsable);
            }

            EstadisticasDTO estadisticas = EstadisticasDTO.builder()
                    .totalReclamaciones((long) reclamacionesArea.size())
                    .totalRecibidas(contarPorEstadoPeriodo(reclamacionesArea, EstadoReclamacion.RECIBIDO))
                    .totalEnProceso(contarPorEstadoPeriodo(reclamacionesArea, EstadoReclamacion.EN_PROCESO))
                    .totalResueltas(contarPorEstadoPeriodo(reclamacionesArea, EstadoReclamacion.RESUELTO))
                    .totalVencidas(calcularVencidasPeriodo(reclamacionesArea))
                    .tiempoPromedioRespuesta(calcularTiempoPromedioRespuestaPeriodo(reclamacionesArea))
                    .tiempoPromedioResolucion(calcularTiempoPromedioResolucionPeriodo(reclamacionesArea))
                    .porcentajeRespuestasATiempo(calcularPorcentajeRespuestasATiempoPeriodo(reclamacionesArea))
                    .porcentajeResolucionesATiempo(calcularPorcentajeResolucionesATiempoPeriodo(reclamacionesArea))
                    .build();

            log.info("Estadísticas del área {} obtenidas exitosamente", areaResponsable);
            return estadisticas;

        } catch (Exception e) {
            log.error("Error al obtener estadísticas por área: {}", e.getMessage(), e);
            return crearEstadisticasVacias();
        }
    }

    // =================== REPORTES DE RECLAMACIONES ===================

    @Override
    public Page<ReclamacionReporteDTO> generarReporteReclamaciones(
            EstadoReclamacion estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            TipoMotivo tipoMotivo,
            String areaResponsable,
            Pageable pageable) {

        log.info("Generando reporte de reclamaciones con filtros");

        try {
            // Construir consulta con filtros dinámicos
            List<Reclamacion> reclamacionesFiltradas = aplicarFiltrosReclamaciones(
                    estado, fechaInicio, fechaFin, tipoMotivo, areaResponsable);

            // Convertir a DTOs de reporte
            List<ReclamacionReporteDTO> reporteDTOs = reclamacionesFiltradas.stream()
                    .map(this::convertirAReclamacionReporte)
                    .toList();

            // Aplicar paginación manual
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), reporteDTOs.size());
            List<ReclamacionReporteDTO> paginatedList = start >= reporteDTOs.size() ? 
                    new ArrayList<>() : reporteDTOs.subList(start, end);

            Page<ReclamacionReporteDTO> resultado = new PageImpl<>(
                    paginatedList, pageable, reporteDTOs.size());

            log.info("Reporte generado con {} registros", resultado.getTotalElements());
            return resultado;

        } catch (Exception e) {
            log.error("Error al generar reporte de reclamaciones: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<ReclamacionReporteDTO> generarReporteReclamacionesVencidas(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin, 
            Pageable pageable) {

        log.info("Generando reporte de reclamaciones vencidas");

        try {
            List<Reclamacion> reclamaciones = fechaInicio != null && fechaFin != null
                    ? reclamacionRepository.findByFechaReclamacionBetween(fechaInicio, fechaFin)
                    : reclamacionRepository.findAll();

            List<ReclamacionReporteDTO> vencidas = reclamaciones.stream()
                    .filter(this::esReclamacionVencida)
                    .map(this::convertirAReclamacionReporte)
                    .toList();

            // Aplicar paginación
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), vencidas.size());
            List<ReclamacionReporteDTO> paginatedList = start >= vencidas.size() ? 
                    new ArrayList<>() : vencidas.subList(start, end);

            Page<ReclamacionReporteDTO> resultado = new PageImpl<>(
                    paginatedList, pageable, vencidas.size());

            log.info("Reporte de vencidas generado con {} registros", resultado.getTotalElements());
            return resultado;

        } catch (Exception e) {
            log.error("Error al generar reporte de vencidas: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    @Override
    public List<Map<String, Object>> generarReporteTiempoRespuesta(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String areaResponsable) {

        log.info("Generando reporte de tiempo de respuesta");

        try {
            List<Reclamacion> reclamaciones = aplicarFiltrosReclamaciones(
                    null, fechaInicio, fechaFin, null, areaResponsable);

            return reclamaciones.stream()
                    .map(this::calcularMetricasTiempoRespuesta)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            log.error("Error al generar reporte de tiempo de respuesta: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // =================== REPORTES DE TENDENCIAS ===================

    @Override
    public Map<String, Long> obtenerTendenciaMensual(int mesesAtras) {
        log.info("Obteniendo tendencia mensual de {} meses", mesesAtras);

        try {
            LocalDateTime fechaInicio = LocalDateTime.now().minusMonths(mesesAtras);
            List<Reclamacion> reclamaciones = reclamacionRepository
                    .findByFechaReclamacionGreaterThanEqual(fechaInicio);

            Map<String, Long> tendencia = new LinkedHashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

            reclamaciones.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getFechaReclamacion().format(formatter),
                            Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> tendencia.put(entry.getKey(), entry.getValue()));

            log.info("Tendencia mensual obtenida con {} períodos", tendencia.size());
            return tendencia;

        } catch (Exception e) {
            log.error("Error al obtener tendencia mensual: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Long> obtenerTendenciaPorTipoMotivo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo tendencia por tipo de motivo");

        try {
            List<Reclamacion> reclamaciones = reclamacionRepository
                    .findByFechaReclamacionBetween(fechaInicio, fechaFin);

            return reclamaciones.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getTipoMotivo() != null ? r.getTipoMotivo().getDescripcion() : "Sin clasificar",
                            Collectors.counting()));

        } catch (Exception e) {
            log.error("Error al obtener tendencia por tipo de motivo: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Long> obtenerTendenciaPorCanal(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo tendencia por canal de recepción");

        try {
            List<Reclamacion> reclamaciones = reclamacionRepository
                    .findByFechaReclamacionBetween(fechaInicio, fechaFin);

            return reclamaciones.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getCanalRecepcion() != null ? r.getCanalRecepcion().getDescripcion() : "Sin especificar",
                            Collectors.counting()));

        } catch (Exception e) {
            log.error("Error al obtener tendencia por canal: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    // =================== REPORTES DE RENDIMIENTO ===================

    @Override
    public List<Map<String, Object>> obtenerReporteRendimientoPorUsuario(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin) {

        log.info("Obteniendo reporte de rendimiento por usuario");

        try {
            List<Reclamacion> reclamaciones = reclamacionRepository
                    .findByFechaReclamacionBetween(fechaInicio, fechaFin);

            Map<String, List<Reclamacion>> reclamacionesPorUsuario = reclamaciones.stream()
                    .filter(r -> StringUtils.hasText(r.getAsignadoA()))
                    .collect(Collectors.groupingBy(Reclamacion::getAsignadoA));

            return reclamacionesPorUsuario.entrySet().stream()
                    .map(entry -> calcularMetricasRendimientoUsuario(entry.getKey(), entry.getValue()))
                    .sorted((a, b) -> Long.compare((Long) b.get("totalAsignadas"), (Long) a.get("totalAsignadas")))
                    .toList();

        } catch (Exception e) {
            log.error("Error al obtener reporte de rendimiento por usuario: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> obtenerReporteRendimientoPorArea(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin) {

        log.info("Obteniendo reporte de rendimiento por área");

        try {
            List<Reclamacion> reclamaciones = reclamacionRepository
                    .findByFechaReclamacionBetween(fechaInicio, fechaFin);

            Map<String, List<Reclamacion>> reclamacionesPorArea = reclamaciones.stream()
                    .filter(r -> StringUtils.hasText(r.getAreaResponsable()))
                    .collect(Collectors.groupingBy(Reclamacion::getAreaResponsable));

            return reclamacionesPorArea.entrySet().stream()
                    .map(entry -> calcularMetricasRendimientoArea(entry.getKey(), entry.getValue()))
                    .sorted((a, b) -> Long.compare((Long) b.get("totalReclamaciones"), (Long) a.get("totalReclamaciones")))
                    .toList();

        } catch (Exception e) {
            log.error("Error al obtener reporte de rendimiento por área: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Double calcularTiempoPromedioRespuesta(LocalDateTime fechaInicio, 
                                                 LocalDateTime fechaFin, 
                                                 String areaResponsable) {
        try {
            List<Reclamacion> reclamaciones = aplicarFiltrosReclamaciones(
                    null, fechaInicio, fechaFin, null, areaResponsable);

            List<Double> tiempos = new ArrayList<>();
            
            for (Reclamacion reclamacion : reclamaciones) {
                Optional<RespuestaReclamacion> primeraRespuesta = respuestaRepository
                        .findFirstByReclamacionIdOrderByFechaRespuesta(reclamacion.getId());
                
                if (primeraRespuesta.isPresent()) {
                    long horas = ChronoUnit.HOURS.between(
                            reclamacion.getFechaReclamacion(),
                            primeraRespuesta.get().getFechaRespuesta());
                    tiempos.add(horas / 24.0); // Convertir a días
                }
            }

            return tiempos.isEmpty() ? 0.0 : tiempos.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

        } catch (Exception e) {
            log.error("Error al calcular tiempo promedio de respuesta: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    // =================== EXPORTACIÓN DE DATOS ===================

    @Override
    public byte[] exportarReclamacionesExcel(Map<String, Object> criterios) {
        log.info("Exportando reclamaciones a Excel");
        
        try {
            // Implementación básica - retorna array vacío
            // En una implementación real usarías Apache POI
            log.warn("Exportación a Excel no implementada completamente");
            return new byte[0];
            
        } catch (Exception e) {
            log.error("Error al exportar a Excel: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] exportarEstadisticasPDF(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Exportando estadísticas a PDF");
        
        try {
            // Implementación básica - retorna array vacío
            // En una implementación real usarías iText o similar
            log.warn("Exportación a PDF no implementada completamente");
            return new byte[0];
            
        } catch (Exception e) {
            log.error("Error al exportar a PDF: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] exportarReporteRendimientoCSV(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Exportando reporte de rendimiento a CSV");
        
        try {
            // Implementación básica - retorna array vacío
            // En una implementación real generarías CSV
            log.warn("Exportación a CSV no implementada completamente");
            return new byte[0];
            
        } catch (Exception e) {
            log.error("Error al exportar a CSV: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    // =================== DASHBOARD Y MÉTRICAS ===================

    @Override
    public Map<String, Object> obtenerDatosDashboard() {
        log.info("Obteniendo datos para dashboard");

        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Métricas principales
            dashboard.put("totalReclamaciones", reclamacionRepository.count());
            dashboard.put("reclamacionesHoy", contarReclamacionesHoy());
            dashboard.put("vencidasHoy", contarVencidasHoy());
            dashboard.put("enProcesoUrgentes", contarEnProcesoUrgentes());
            
            // Distribución por estado
            dashboard.put("distribucionEstados", obtenerContadoresPorEstado());
            
            // Últimas reclamaciones
            dashboard.put("ultimasReclamaciones", obtenerUltimasReclamaciones(5));
            
            // Alertas
            dashboard.put("alertas", obtenerAlertasGestion());

            log.info("Datos de dashboard obtenidos exitosamente");
            return dashboard;

        } catch (Exception e) {
            log.error("Error al obtener datos de dashboard: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> obtenerMetricasTiempoReal() {
        log.info("Obteniendo métricas en tiempo real");

        try {
            Map<String, Object> metricas = new HashMap<>();
            
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime inicioMes = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            
            metricas.put("reclamacionesMesActual", 
                    reclamacionRepository.countByFechaReclamacionGreaterThanEqual(inicioMes));
            metricas.put("respuestasHoy", contarRespuestasHoy());
            metricas.put("tiempoPromedioRespuestaHoy", calcularTiempoPromedioRespuestaHoy());
            metricas.put("satisfaccionPromedio", calcularSatisfaccionPromedio());

            return metricas;

        } catch (Exception e) {
            log.error("Error al obtener métricas en tiempo real: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> obtenerTopAreasPorReclamaciones(int limite) {
        log.info("Obteniendo top {} áreas por reclamaciones", limite);

        try {
            List<Reclamacion> todasReclamaciones = reclamacionRepository.findAll();
            
            return todasReclamaciones.stream()
                    .filter(r -> StringUtils.hasText(r.getAreaResponsable()))
                    .collect(Collectors.groupingBy(
                            Reclamacion::getAreaResponsable,
                            Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(limite)
                    .map(entry -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("area", entry.getKey());
                        item.put("cantidad", entry.getValue());
                        return item;
                    })
                    .toList();

        } catch (Exception e) {
            log.error("Error al obtener top áreas: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> obtenerAlertasGestion() {
        log.info("Obteniendo alertas de gestión");

        try {
            List<Map<String, Object>> alertas = new ArrayList<>();
            
            // Alertas de vencimiento
            long vencidas = calcularTotalVencidas();
            if (vencidas > 0) {
                Map<String, Object> alerta = new HashMap<>();
                alerta.put("tipo", "VENCIMIENTO");
                alerta.put("mensaje", String.format("%d reclamaciones vencidas requieren atención", vencidas));
                alerta.put("cantidad", vencidas);
                alerta.put("prioridad", "ALTA");
                alertas.add(alerta);
            }
            
            // Alertas de sobrecarga por área
            obtenerAreasSobrecargadas().forEach(alertas::add);
            
            // Alertas de tiempo de respuesta
            if (calcularPorcentajeRespuestasATiempo() < 80.0) {
                Map<String, Object> alerta = new HashMap<>();
                alerta.put("tipo", "TIEMPO_RESPUESTA");
                alerta.put("mensaje", "El tiempo de respuesta promedio está por debajo del objetivo");
                alerta.put("prioridad", "MEDIA");
                alertas.add(alerta);
            }

            log.info("Se generaron {} alertas de gestión", alertas.size());
            return alertas;

        } catch (Exception e) {
            log.error("Error al obtener alertas de gestión: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // =================== MÉTODOS PRIVADOS DE APOYO ===================

    private Long contarPorEstado(EstadoReclamacion estado) {
        return reclamacionRepository.countByEstado(estado);
    }

    private Long contarPorEstadoPeriodo(List<Reclamacion> reclamaciones, EstadoReclamacion estado) {
        return reclamaciones.stream()
                .filter(r -> r.getEstado() == estado)
                .count();
    }

    private Long calcularTotalVencidas() {
        return reclamacionRepository.findAll().stream()
                .filter(this::esReclamacionVencida)
                .count();
    }

    private Long calcularVencidasPeriodo(List<Reclamacion> reclamaciones) {
        return reclamaciones.stream()
                .filter(this::esReclamacionVencida)
                .count();
    }

    private boolean esReclamacionVencida(Reclamacion reclamacion) {
        if (reclamacion.getFechaLimiteRespuesta() == null) {
            return false;
        }
        
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(reclamacion.getFechaLimiteRespuesta()) && 
               !Arrays.asList(EstadoReclamacion.RESUELTO, EstadoReclamacion.CERRADO, EstadoReclamacion.ANULADO)
                       .contains(reclamacion.getEstado());
    }

    private Map<String, Long> obtenerContadoresPorTipoMotivo() {
        return reclamacionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getTipoMotivo() != null ? r.getTipoMotivo().getDescripcion() : "Sin clasificar",
                        Collectors.counting()));
    }

    private Map<String, Long> obtenerContadoresPorCanal() {
        return reclamacionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCanalRecepcion() != null ? r.getCanalRecepcion().getDescripcion() : "Sin especificar",
                        Collectors.counting()));
    }

    private Map<String, Long> obtenerContadoresPorEstado() {
        return reclamacionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getEstado().getDescripcion(),
                        Collectors.counting()));
    }

    private Map<String, Long> obtenerContadoresPorArea() {
        return reclamacionRepository.findAll().stream()
                .filter(r -> StringUtils.hasText(r.getAreaResponsable()))
                .collect(Collectors.groupingBy(
                        Reclamacion::getAreaResponsable,
                        Collectors.counting()));
    }

    private Map<String, Long> obtenerContadoresPorTipoMotivoPeriodo(List<Reclamacion> reclamaciones) {
        return reclamaciones.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getTipoMotivo() != null ? r.getTipoMotivo().getDescripcion() : "Sin clasificar",
                        Collectors.counting()));
    }

    private Map<String, Long> obtenerContadoresPorCanalPeriodo(List<Reclamacion> reclamaciones) {
        return reclamaciones.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCanalRecepcion() != null ? r.getCanalRecepcion().getDescripcion() : "Sin especificar",
                        Collectors.counting()));
    }

    private Map<String, Long> obtenerContadoresPorEstadoPeriodo(List<Reclamacion> reclamaciones) {
        return reclamaciones.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getEstado().getDescripcion(),
                        Collectors.counting()));
    }

    private Map<String, Long> obtenerContadoresPorAreaPeriodo(List<Reclamacion> reclamaciones) {
        return reclamaciones.stream()
                .filter(r -> StringUtils.hasText(r.getAreaResponsable()))
                .collect(Collectors.groupingBy(
                        Reclamacion::getAreaResponsable,
                        Collectors.counting()));
    }

    private Double calcularTiempoPromedioRespuestaGlobal() {
        return calcularTiempoPromedioRespuesta(null, null, null);
    }

    private Double calcularTiempoPromedioResolucionGlobal() {
        try {
            List<Reclamacion> resueltas = reclamacionRepository.findByEstado(EstadoReclamacion.RESUELTO);
            List<Double> tiempos = new ArrayList<>();
            
            for (Reclamacion reclamacion : resueltas) {
                Optional<RespuestaReclamacion> respuestasFinales = respuestaRepository
                        .findByReclamacionIdAndEsRespuestaFinalTrue(reclamacion.getId());
                
                if (respuestasFinales.isPresent()) {
                    long horas = ChronoUnit.HOURS.between(
                            reclamacion.getFechaReclamacion(),
                            respuestasFinales.get().getFechaRespuesta());
                    tiempos.add(horas / 24.0);
                }
            }

            return tiempos.isEmpty() ? 0.0 : tiempos.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

        } catch (Exception e) {
            log.error("Error al calcular tiempo promedio de resolución: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    private Double calcularTiempoPromedioRespuestaPeriodo(List<Reclamacion> reclamaciones) {
        try {
            List<Double> tiempos = new ArrayList<>();
            
            for (Reclamacion reclamacion : reclamaciones) {
                Optional<RespuestaReclamacion> primeraRespuesta = respuestaRepository
                        .findFirstByReclamacionIdOrderByFechaRespuesta(reclamacion.getId());
                
                if (primeraRespuesta.isPresent()) {
                    long horas = ChronoUnit.HOURS.between(
                            reclamacion.getFechaReclamacion(),
                            primeraRespuesta.get().getFechaRespuesta());
                    tiempos.add(horas / 24.0);
                }
            }

            return tiempos.isEmpty() ? 0.0 : tiempos.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

        } catch (Exception e) {
            log.error("Error al calcular tiempo promedio de respuesta del período: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    private Double calcularTiempoPromedioResolucionPeriodo(List<Reclamacion> reclamaciones) {
        try {
            List<Double> tiempos = new ArrayList<>();
            
            for (Reclamacion reclamacion : reclamaciones) {
                if (reclamacion.getEstado() == EstadoReclamacion.RESUELTO) {
                    Optional<RespuestaReclamacion> respuestasFinales = respuestaRepository
                            .findByReclamacionIdAndEsRespuestaFinalTrue(reclamacion.getId());
                    
                    if (respuestasFinales.isPresent()) {
                        long horas = ChronoUnit.HOURS.between(
                                reclamacion.getFechaReclamacion(),
                                respuestasFinales.get().getFechaRespuesta());
                        tiempos.add(horas / 24.0);
                    }
                }
            }

            return tiempos.isEmpty() ? 0.0 : tiempos.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

        } catch (Exception e) {
            log.error("Error al calcular tiempo promedio de resolución del período: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    private Double calcularPorcentajeRespuestasATiempo() {
        try {
            List<Reclamacion> todasReclamaciones = reclamacionRepository.findAll();
            long totalConRespuesta = 0;
            long aTiempo = 0;
            
            for (Reclamacion reclamacion : todasReclamaciones) {
                Optional<RespuestaReclamacion> primeraRespuesta = respuestaRepository
                        .findFirstByReclamacionIdOrderByFechaRespuesta(reclamacion.getId());
                
                if (primeraRespuesta.isPresent()) {
                    totalConRespuesta++;
                    if (reclamacion.getFechaLimiteRespuesta() != null &&
                        primeraRespuesta.get().getFechaRespuesta().isBefore(reclamacion.getFechaLimiteRespuesta())) {
                        aTiempo++;
                    }
                }
            }

            return totalConRespuesta == 0 ? 0.0 : (aTiempo * 100.0) / totalConRespuesta;

        } catch (Exception e) {
            log.error("Error al calcular porcentaje de respuestas a tiempo: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    private Double calcularPorcentajeResolucionesATiempo() {
        try {
            List<Reclamacion> resueltas = reclamacionRepository.findByEstado(EstadoReclamacion.RESUELTO);
            long aTiempo = 0;
            
            for (Reclamacion reclamacion : resueltas) {
                Optional<RespuestaReclamacion> respuestasFinales = respuestaRepository
                        .findByReclamacionIdAndEsRespuestaFinalTrue(reclamacion.getId());
                
                if (respuestasFinales.isPresent() && reclamacion.getFechaLimiteRespuesta() != null &&
                    respuestasFinales.get().getFechaRespuesta().isBefore(reclamacion.getFechaLimiteRespuesta())) {
                    aTiempo++;
                }
            }

            return resueltas.isEmpty() ? 0.0 : (aTiempo * 100.0) / resueltas.size();

        } catch (Exception e) {
            log.error("Error al calcular porcentaje de resoluciones a tiempo: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    private Double calcularPorcentajeRespuestasATiempoPeriodo(List<Reclamacion> reclamaciones) {
        try {
            long totalConRespuesta = 0;
            long aTiempo = 0;
            
            for (Reclamacion reclamacion : reclamaciones) {
                Optional<RespuestaReclamacion> primeraRespuesta = respuestaRepository
                        .findFirstByReclamacionIdOrderByFechaRespuesta(reclamacion.getId());
                
                if (primeraRespuesta.isPresent()) {
                    totalConRespuesta++;
                    if (reclamacion.getFechaLimiteRespuesta() != null &&
                        primeraRespuesta.get().getFechaRespuesta().isBefore(reclamacion.getFechaLimiteRespuesta())) {
                        aTiempo++;
                    }
                }
            }

            return totalConRespuesta == 0 ? 0.0 : (aTiempo * 100.0) / totalConRespuesta;

        } catch (Exception e) {
            log.error("Error al calcular porcentaje de respuestas a tiempo del período: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    private Double calcularPorcentajeResolucionesATiempoPeriodo(List<Reclamacion> reclamaciones) {
        try {
            List<Reclamacion> resueltas = reclamaciones.stream()
                    .filter(r -> r.getEstado() == EstadoReclamacion.RESUELTO)
                    .toList();
            
            long aTiempo = 0;
            
            for (Reclamacion reclamacion : resueltas) {
                Optional<RespuestaReclamacion> respuestasFinales = respuestaRepository
                        .findByReclamacionIdAndEsRespuestaFinalTrue(reclamacion.getId());
                
                if (respuestasFinales.isPresent() && reclamacion.getFechaLimiteRespuesta() != null &&
                    respuestasFinales.get().getFechaRespuesta().isBefore(reclamacion.getFechaLimiteRespuesta())) {
                    aTiempo++;
                }
            }

            return resueltas.isEmpty() ? 0.0 : (aTiempo * 100.0) / resueltas.size();

        } catch (Exception e) {
            log.error("Error al calcular porcentaje de resoluciones a tiempo del período: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    private Map<String, Long> obtenerTendenciaUltimos30Dias() {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        List<Reclamacion> reclamaciones = reclamacionRepository
                .findByFechaReclamacionGreaterThanEqual(hace30Dias);

        Map<String, Long> tendencia = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        reclamaciones.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getFechaReclamacion().format(formatter),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> tendencia.put(entry.getKey(), entry.getValue()));

        return tendencia;
    }

    private Map<String, Long> obtenerTopAreasReclamaciones(int limite) {
        return reclamacionRepository.findAll().stream()
                .filter(r -> StringUtils.hasText(r.getAreaResponsable()))
                .collect(Collectors.groupingBy(
                        Reclamacion::getAreaResponsable,
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private Double calcularPuntuacionPromedioCerradas() {
        // Implementación básica - en un sistema real tendríamos campo de satisfacción
        return 4.2; // Valor de ejemplo
    }

    private EstadisticasDTO crearEstadisticasVacias() {
        return EstadisticasDTO.builder()
                .totalReclamaciones(0L)
                .totalRecibidas(0L)
                .totalEnProceso(0L)
                .totalResueltas(0L)
                .totalVencidas(0L)
                .reclamacionesPorTipoMotivo(new HashMap<>())
                .reclamacionesPorCanal(new HashMap<>())
                .reclamacionesPorEstado(new HashMap<>())
                .reclamacionesPorArea(new HashMap<>())
                .tiempoPromedioRespuesta(0.0)
                .tiempoPromedioResolucion(0.0)
                .porcentajeRespuestasATiempo(0.0)
                .porcentajeResolucionesATiempo(0.0)
                .tendenciaUltimos30Dias(new HashMap<>())
                .topAreasConMasReclamaciones(new HashMap<>())
                .puntuacionPromedioCerradas(0.0)
                .build();
    }

    private List<Reclamacion> aplicarFiltrosReclamaciones(
            EstadoReclamacion estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            TipoMotivo tipoMotivo,
            String areaResponsable) {

        List<Reclamacion> reclamaciones = reclamacionRepository.findAll();

        return reclamaciones.stream()
                .filter(r -> estado == null || r.getEstado() == estado)
                .filter(r -> fechaInicio == null || r.getFechaReclamacion().isAfter(fechaInicio))
                .filter(r -> fechaFin == null || r.getFechaReclamacion().isBefore(fechaFin))
                .filter(r -> tipoMotivo == null || r.getTipoMotivo() == tipoMotivo)
                .filter(r -> areaResponsable == null || areaResponsable.equals(r.getAreaResponsable()))
                .toList();
    }

    private ReclamacionReporteDTO convertirAReclamacionReporte(Reclamacion reclamacion) {
        return ReclamacionReporteDTO.builder()
                .numeroReclamacion(reclamacion.getNumeroReclamacion())
                .fechaReclamacion(reclamacion.getFechaReclamacion())
                .tipoMotivo(reclamacion.getTipoMotivo() != null ? reclamacion.getTipoMotivo().getDescripcion() : "Sin clasificar")
                .nombreReclamante(reclamacion.getNombreReclamante())
                .canalRecepcion(reclamacion.getCanalRecepcion() != null ? reclamacion.getCanalRecepcion().getDescripcion() : "Sin especificar")
                .estado(reclamacion.getEstado().getDescripcion())
                .prioridad(reclamacion.getPrioridad() != null ? reclamacion.getPrioridad().getDescripcion() : "Normal")
                .areaResponsable(reclamacion.getAreaResponsable())
                .asignadoA(reclamacion.getAsignadoA())
                .fechaLimiteRespuesta(reclamacion.getFechaLimiteRespuesta())
                .esVencida(esReclamacionVencida(reclamacion))
                .diasVencidos(calcularDiasVencidos(reclamacion))
                .totalRespuestas(Math.toIntExact(respuestaRepository.countByReclamacionId(reclamacion.getId())))
                .descripcionResumida(reclamacion.getDescripcion() != null && reclamacion.getDescripcion().length() > 100
                        ? reclamacion.getDescripcion().substring(0, 100) + "..."
                        : reclamacion.getDescripcion())
                .build();
    }

    private Integer calcularDiasVencidos(Reclamacion reclamacion) {
        if (reclamacion.getFechaLimiteRespuesta() == null || !esReclamacionVencida(reclamacion)) {
            return 0;
        }
        
        return Math.toIntExact(ChronoUnit.DAYS.between(
                reclamacion.getFechaLimiteRespuesta(), 
                LocalDateTime.now()));
    }

    private Map<String, Object> calcularMetricasTiempoRespuesta(Reclamacion reclamacion) {
        try {
            Optional<RespuestaReclamacion> primeraRespuesta = respuestaRepository
                    .findFirstByReclamacionIdOrderByFechaRespuesta(reclamacion.getId());
            
            if (primeraRespuesta.isEmpty()) {
                return null;
            }

            Map<String, Object> metricas = new HashMap<>();
            long horas = ChronoUnit.HOURS.between(
                    reclamacion.getFechaReclamacion(),
                    primeraRespuesta.get().getFechaRespuesta());
            
            metricas.put("numeroReclamacion", reclamacion.getNumeroReclamacion());
            metricas.put("fechaReclamacion", reclamacion.getFechaReclamacion());
            metricas.put("fechaPrimeraRespuesta", primeraRespuesta.get().getFechaRespuesta());
            metricas.put("tiempoRespuestaHoras", horas);
            metricas.put("tiempoRespuestaDias", horas / 24.0);
            metricas.put("areaResponsable", reclamacion.getAreaResponsable());
            metricas.put("aTiempo", reclamacion.getFechaLimiteRespuesta() != null && 
                    primeraRespuesta.get().getFechaRespuesta().isBefore(reclamacion.getFechaLimiteRespuesta()));
            
            return metricas;

        } catch (Exception e) {
            log.error("Error al calcular métricas de tiempo de respuesta para reclamación {}: {}", 
                    reclamacion.getId(), e.getMessage());
            return null;
        }
    }

    private Map<String, Object> calcularMetricasRendimientoUsuario(String usuario, List<Reclamacion> reclamaciones) {
        Map<String, Object> metricas = new HashMap<>();
        
        metricas.put("usuario", usuario);
        metricas.put("totalAsignadas", (long) reclamaciones.size());
        metricas.put("resueltas", reclamaciones.stream().filter(r -> r.getEstado() == EstadoReclamacion.RESUELTO).count());
        metricas.put("enProceso", reclamaciones.stream().filter(r -> r.getEstado() == EstadoReclamacion.EN_PROCESO).count());
        metricas.put("vencidas", reclamaciones.stream().filter(this::esReclamacionVencida).count());
        
        // Tiempo promedio de respuesta para este usuario
        List<Double> tiemposRespuesta = new ArrayList<>();
        for (Reclamacion reclamacion : reclamaciones) {
            Optional<RespuestaReclamacion> primeraRespuesta = respuestaRepository
                    .findFirstByReclamacionIdOrderByFechaRespuesta(reclamacion.getId());
            
            if (primeraRespuesta.isPresent()) {
                long horas = ChronoUnit.HOURS.between(
                        reclamacion.getFechaReclamacion(),
                        primeraRespuesta.get().getFechaRespuesta());
                tiemposRespuesta.add(horas / 24.0);
            }
        }
        
        metricas.put("tiempoPromedioRespuesta", tiemposRespuesta.isEmpty() ? 0.0 : 
                tiemposRespuesta.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        
        return metricas;
    }

    private Map<String, Object> calcularMetricasRendimientoArea(String area, List<Reclamacion> reclamaciones) {
        Map<String, Object> metricas = new HashMap<>();
        
        metricas.put("area", area);
        metricas.put("totalReclamaciones", (long) reclamaciones.size());
        metricas.put("resueltas", reclamaciones.stream().filter(r -> r.getEstado() == EstadoReclamacion.RESUELTO).count());
        metricas.put("enProceso", reclamaciones.stream().filter(r -> r.getEstado() == EstadoReclamacion.EN_PROCESO).count());
        metricas.put("vencidas", reclamaciones.stream().filter(this::esReclamacionVencida).count());
        
        // Calcular tiempo promedio de resolución
        List<Double> tiemposResolucion = new ArrayList<>();
        for (Reclamacion reclamacion : reclamaciones) {
            if (reclamacion.getEstado() == EstadoReclamacion.RESUELTO) {
                Optional<RespuestaReclamacion> respuestasFinales = respuestaRepository
                        .findByReclamacionIdAndEsRespuestaFinalTrue(reclamacion.getId());
                
                if (respuestasFinales.isPresent()) {
                    long horas = ChronoUnit.HOURS.between(
                            reclamacion.getFechaReclamacion(),
                            respuestasFinales.get().getFechaRespuesta());
                    tiemposResolucion.add(horas / 24.0);
                }
            }
        }
        
        metricas.put("tiempoPromedioResolucion", tiemposResolucion.isEmpty() ? 0.0 : 
                tiemposResolucion.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        
        return metricas;
    }

    private long contarReclamacionesHoy() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = inicioHoy.plusDays(1);
        return reclamacionRepository.countByFechaReclamacionBetween(inicioHoy, finHoy);
    }

    private long contarVencidasHoy() {
        LocalDateTime hoy = LocalDateTime.now();
        return reclamacionRepository.findAll().stream()
                .filter(r -> r.getFechaLimiteRespuesta() != null && 
                        r.getFechaLimiteRespuesta().toLocalDate().equals(hoy.toLocalDate()) &&
                        esReclamacionVencida(r))
                .count();
    }

    private long contarEnProcesoUrgentes() {
        return reclamacionRepository.findByEstado(EstadoReclamacion.EN_PROCESO).stream()
                .filter(r -> r.getPrioridad() != null && r.getPrioridad().name().equals("URGENTE"))
                .count();
    }

    private List<Map<String, Object>> obtenerUltimasReclamaciones(int limite) {
        return reclamacionRepository.findTop5ByOrderByFechaReclamacionDesc().stream()
                .limit(limite)
                .map(r -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("numeroReclamacion", r.getNumeroReclamacion());
                    item.put("fechaReclamacion", r.getFechaReclamacion());
                    item.put("nombreReclamante", r.getNombreReclamante());
                    item.put("estado", r.getEstado().getDescripcion());
                    item.put("areaResponsable", r.getAreaResponsable());
                    return item;
                })
                .toList();
    }

    private long contarRespuestasHoy() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = inicioHoy.plusDays(1);
        return respuestaRepository.findByFechaRespuestaBetween(inicioHoy, finHoy).size();
    }

    private Double calcularTiempoPromedioRespuestaHoy() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = inicioHoy.plusDays(1);
        
        List<RespuestaReclamacion> respuestasHoy = respuestaRepository
                .findByFechaRespuestaBetween(inicioHoy, finHoy);
        
        List<Double> tiempos = new ArrayList<>();
        for (RespuestaReclamacion respuesta : respuestasHoy) {
            long horas = ChronoUnit.HOURS.between(
                    respuesta.getReclamacion().getFechaReclamacion(),
                    respuesta.getFechaRespuesta());
            tiempos.add(horas / 24.0);
        }
        
        return tiempos.isEmpty() ? 0.0 : tiempos.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private Double calcularSatisfaccionPromedio() {
        // Implementación básica - en un sistema real se calcularía de encuestas
        return 4.1; // Valor de ejemplo
    }

    private List<Map<String, Object>> obtenerAreasSobrecargadas() {
        List<Map<String, Object>> alertas = new ArrayList<>();
        
        Map<String, Long> reclamacionesPorArea = obtenerContadoresPorArea();
        long promedioReclamaciones = (long) reclamacionesPorArea.values().stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        
        reclamacionesPorArea.entrySet().stream()
                .filter(entry -> entry.getValue() > promedioReclamaciones * 1.5) // 50% más que el promedio
                .forEach(entry -> {
                    Map<String, Object> alerta = new HashMap<>();
                    alerta.put("tipo", "SOBRECARGA_AREA");
                    alerta.put("mensaje", String.format("El área '%s' tiene %d reclamaciones (sobrecargada)", 
                            entry.getKey(), entry.getValue()));
                    alerta.put("area", entry.getKey());
                    alerta.put("cantidad", entry.getValue());
                    alerta.put("prioridad", "MEDIA");
                    alertas.add(alerta);
                });
        
        return alertas;
    }
}
