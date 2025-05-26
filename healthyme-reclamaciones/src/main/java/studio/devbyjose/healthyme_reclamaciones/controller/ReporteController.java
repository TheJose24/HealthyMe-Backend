package studio.devbyjose.healthyme_reclamaciones.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import studio.devbyjose.healthyme_reclamaciones.dto.*;
import studio.devbyjose.healthyme_reclamaciones.enums.*;
import studio.devbyjose.healthyme_reclamaciones.service.interfaces.ReporteService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la generación de reportes y estadísticas
 * Proporciona endpoints para análisis de datos, dashboard y exportación
 */
@Tag(
    name = "Reportes y Estadísticas", 
    description = "API para generación de reportes, estadísticas y dashboard de reclamaciones"
)
@Slf4j
@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReporteController {

    private final ReporteService reporteService;

    // =================== ESTADÍSTICAS GENERALES ===================

    @Operation(
        summary = "Obtener estadísticas generales",
        description = "Recupera las estadísticas completas del sistema de reclamaciones"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estadísticas obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = EstadisticasDTO.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/estadisticas/generales")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticasGenerales() {
        
        log.info("Generando estadísticas generales del sistema");
        
        EstadisticasDTO estadisticas = reporteService.obtenerEstadisticasGenerales();
        
        log.info("Estadísticas generadas exitosamente");
        
        return ResponseEntity.ok(estadisticas);
    }

    @Operation(
        summary = "Obtener estadísticas por período",
        description = "Recupera estadísticas filtradas por un rango de fechas específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estadísticas del período obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = EstadisticasDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Rango de fechas inválido",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/estadisticas/periodo")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticasPorPeriodo(
            @Parameter(description = "Fecha de inicio del período", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin del período", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin) {
        
        log.info("Generando estadísticas del período: {} a {}", fecha_inicio, fecha_fin);
        
        EstadisticasDTO estadisticas = reporteService.obtenerEstadisticasPorPeriodo(fecha_inicio, fecha_fin);
        
        log.info("Estadísticas del período generadas exitosamente");
        
        return ResponseEntity.ok(estadisticas);
    }

    @Operation(
        summary = "Obtener estadísticas por área",
        description = "Recupera estadísticas específicas de un área responsable"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estadísticas del área obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = EstadisticasDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Área no encontrada",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/estadisticas/area/{area_responsable}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticasPorArea(
            @Parameter(description = "Área responsable", required = true, example = "Atención al Cliente")
            @PathVariable String area_responsable,
            
            @Parameter(description = "Fecha de inicio (opcional)", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin (opcional)", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin) {
        
        log.info("Generando estadísticas del área: {}", area_responsable);
        
        EstadisticasDTO estadisticas = reporteService.obtenerEstadisticasPorArea(
                area_responsable, fecha_inicio, fecha_fin);
        
        log.info("Estadísticas del área {} generadas exitosamente", area_responsable);
        
        return ResponseEntity.ok(estadisticas);
    }

    // =================== REPORTES DE RECLAMACIONES ===================

    @Operation(
        summary = "Generar reporte de reclamaciones",
        description = "Genera un reporte detallado de reclamaciones basado en criterios específicos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reporte generado exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Criterios de búsqueda inválidos",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/reclamaciones")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERADOR', 'CONSULTA')")
    public ResponseEntity<Page<ReclamacionReporteDTO>> generarReporteReclamaciones(
            @Parameter(description = "Estado de las reclamaciones")
            @RequestParam(required = false) EstadoReclamacion estado,
            
            @Parameter(description = "Fecha de inicio", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin,
            
            @Parameter(description = "Tipo de motivo")
            @RequestParam(required = false) TipoMotivo tipo_motivo,
            
            @Parameter(description = "Área responsable", example = "Atención al Cliente")
            @RequestParam(required = false) String area_responsable,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 50, sort = "fechaReclamacion") Pageable pageable) {
        
        log.info("Generando reporte de reclamaciones con criterios: estado={}, fechaInicio={}, fechaFin={}, tipoMotivo={}, area={}", 
                estado, fecha_inicio, fecha_fin, tipo_motivo, area_responsable);
        
        Page<ReclamacionReporteDTO> reporte = reporteService.generarReporteReclamaciones(
                estado, fecha_inicio, fecha_fin, tipo_motivo, area_responsable, pageable);
        
        log.info("Reporte de reclamaciones generado: {} registros encontrados", reporte.getTotalElements());
        
        return ResponseEntity.ok(reporte);
    }

    @Operation(
        summary = "Generar reporte de reclamaciones vencidas",
        description = "Genera un reporte específico de reclamaciones que han superado su tiempo límite"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reporte de vencidas generado exitosamente",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/reclamaciones/vencidas")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERADOR', 'CONSULTA')")
    public ResponseEntity<Page<ReclamacionReporteDTO>> generarReporteReclamacionesVencidas(
            @Parameter(description = "Fecha de inicio", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin,
            
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 50, sort = "diasVencidos") Pageable pageable) {
        
        log.info("Generando reporte de reclamaciones vencidas");
        
        Page<ReclamacionReporteDTO> reporteVencidas = reporteService.generarReporteReclamacionesVencidas(
                fecha_inicio, fecha_fin, pageable);
        
        log.info("Reporte de vencidas generado: {} reclamaciones vencidas encontradas", 
                reporteVencidas.getTotalElements());
        
        return ResponseEntity.ok(reporteVencidas);
    }

    @Operation(
        summary = "Generar reporte de tiempo de respuesta",
        description = "Analiza los tiempos de respuesta y genera métricas de rendimiento"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reporte de tiempos generado exitosamente",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/tiempo-respuesta")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<List<Map<String, Object>>> generarReporteTiempoRespuesta(
            @Parameter(description = "Fecha de inicio", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin,
            
            @Parameter(description = "Área responsable (opcional)", example = "Atención al Cliente")
            @RequestParam(required = false) String area_responsable) {
        
        log.info("Generando reporte de tiempo de respuesta: {} a {}", fecha_inicio, fecha_fin);
        
        List<Map<String, Object>> reporteTiempos = reporteService.generarReporteTiempoRespuesta(
                fecha_inicio, fecha_fin, area_responsable);
        
        log.info("Reporte de tiempo de respuesta generado exitosamente");
        
        return ResponseEntity.ok(reporteTiempos);
    }

    // =================== REPORTES DE TENDENCIAS ===================

    @Operation(
        summary = "Obtener tendencia mensual",
        description = "Recupera la tendencia de reclamaciones por mes en un período específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tendencia mensual obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/tendencias/mensual")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Long>> obtenerTendenciaMensual(
            @Parameter(description = "Cantidad de meses hacia atrás", example = "12")
            @RequestParam(defaultValue = "12") @Min(1) @Max(24) int meses_atras) {
        
        log.info("Obteniendo tendencia mensual de {} meses atrás", meses_atras);
        
        Map<String, Long> tendencia = reporteService.obtenerTendenciaMensual(meses_atras);
        
        return ResponseEntity.ok(tendencia);
    }

    @Operation(
        summary = "Obtener tendencia por tipo de motivo",
        description = "Analiza la distribución de reclamaciones por tipo de motivo en un período"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tendencia por tipo obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/tendencias/tipo-motivo")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Long>> obtenerTendenciaPorTipoMotivo(
            @Parameter(description = "Fecha de inicio", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin) {
        
        log.info("Obteniendo tendencia por tipo de motivo: {} a {}", fecha_inicio, fecha_fin);
        
        Map<String, Long> tendencia = reporteService.obtenerTendenciaPorTipoMotivo(fecha_inicio, fecha_fin);
        
        return ResponseEntity.ok(tendencia);
    }

    @Operation(
        summary = "Obtener tendencia por canal de recepción",
        description = "Analiza la distribución de reclamaciones por canal de recepción"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tendencia por canal obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/tendencias/canal")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Long>> obtenerTendenciaPorCanal(
            @Parameter(description = "Fecha de inicio", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin) {
        
        log.info("Obteniendo tendencia por canal: {} a {}", fecha_inicio, fecha_fin);
        
        Map<String, Long> tendencia = reporteService.obtenerTendenciaPorCanal(fecha_inicio, fecha_fin);
        
        return ResponseEntity.ok(tendencia);
    }

    // =================== REPORTES DE RENDIMIENTO ===================

    @Operation(
        summary = "Obtener reporte de rendimiento por usuario",
        description = "Genera métricas de rendimiento individual de usuarios"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reporte de rendimiento por usuario obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/rendimiento/usuarios")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<Map<String, Object>>> obtenerReporteRendimientoPorUsuario(
            @Parameter(description = "Fecha de inicio", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin) {
        
        log.info("Generando reporte de rendimiento por usuario: {} a {}", fecha_inicio, fecha_fin);
        
        List<Map<String, Object>> rendimiento = reporteService.obtenerReporteRendimientoPorUsuario(
                fecha_inicio, fecha_fin);
        
        log.info("Reporte de rendimiento por usuario generado exitosamente");
        
        return ResponseEntity.ok(rendimiento);
    }

    @Operation(
        summary = "Obtener reporte de rendimiento por área",
        description = "Genera métricas de rendimiento por área responsable"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reporte de rendimiento por área obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/rendimiento/areas")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<Map<String, Object>>> obtenerReporteRendimientoPorArea(
            @Parameter(description = "Fecha de inicio", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin) {
        
        log.info("Generando reporte de rendimiento por área: {} a {}", fecha_inicio, fecha_fin);
        
        List<Map<String, Object>> rendimiento = reporteService.obtenerReporteRendimientoPorArea(
                fecha_inicio, fecha_fin);
        
        log.info("Reporte de rendimiento por área generado exitosamente");
        
        return ResponseEntity.ok(rendimiento);
    }

    @Operation(
        summary = "Calcular tiempo promedio de respuesta",
        description = "Calcula el tiempo promedio de respuesta en días para un período específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tiempo promedio calculado exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/rendimiento/tiempo-promedio")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Double>> calcularTiempoPromedioRespuesta(
            @Parameter(description = "Fecha de inicio", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin,
            
            @Parameter(description = "Área responsable (opcional)", example = "Atención al Cliente")
            @RequestParam(required = false) String area_responsable) {
        
        log.info("Calculando tiempo promedio de respuesta: {} a {}", fecha_inicio, fecha_fin);
        
        Double tiempoPromedio = reporteService.calcularTiempoPromedioRespuesta(
                fecha_inicio, fecha_fin, area_responsable);
        
        return ResponseEntity.ok(Map.of("tiempoPromedioDias", tiempoPromedio != null ? tiempoPromedio : 0.0));
    }

    // =================== EXPORTACIÓN DE DATOS ===================

    @Operation(
        summary = "Exportar reclamaciones a Excel",
        description = "Exporta un reporte de reclamaciones en formato Excel"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Archivo Excel generado exitosamente",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error al generar el archivo",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/exportar/excel")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<byte[]> exportarReclamacionesExcel(
            @Parameter(description = "Estado de las reclamaciones")
            @RequestParam(required = false) EstadoReclamacion estado,
            
            @Parameter(description = "Fecha de inicio", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin,
            
            @Parameter(description = "Área responsable", example = "Atención al Cliente")
            @RequestParam(required = false) String area_responsable) {
        
        log.info("Exportando reclamaciones a Excel");
        
        Map<String, Object> criterios = Map.of(
            "estado", estado != null ? estado : "",
            "fechaInicio", fecha_inicio != null ? fecha_inicio : "",
            "fechaFin", fecha_fin != null ? fecha_fin : "",
            "areaResponsable", area_responsable != null ? area_responsable : ""
        );
        
        byte[] excelData = reporteService.exportarReclamacionesExcel(criterios);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reclamaciones_reporte.xlsx");
        
        log.info("Archivo Excel de reclamaciones generado exitosamente");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @Operation(
        summary = "Exportar estadísticas a PDF",
        description = "Exporta un reporte de estadísticas en formato PDF"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Archivo PDF generado exitosamente",
            content = @Content(mediaType = "application/pdf")
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error al generar el archivo",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/exportar/estadisticas-pdf")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<byte[]> exportarEstadisticasPDF(
            @Parameter(description = "Fecha de inicio", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin) {
        
        log.info("Exportando estadísticas a PDF: {} a {}", fecha_inicio, fecha_fin);
        
        byte[] pdfData = reporteService.exportarEstadisticasPDF(fecha_inicio, fecha_fin);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "estadisticas_reporte.pdf");
        
        log.info("Archivo PDF de estadísticas generado exitosamente");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }

    @Operation(
        summary = "Exportar rendimiento a CSV",
        description = "Exporta un reporte de rendimiento en formato CSV"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Archivo CSV generado exitosamente",
            content = @Content(mediaType = "text/csv")
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error al generar el archivo",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/exportar/rendimiento-csv")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<byte[]> exportarReporteRendimientoCSV(
            @Parameter(description = "Fecha de inicio", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @NotNull 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin) {
        
        log.info("Exportando reporte de rendimiento a CSV: {} a {}", fecha_inicio, fecha_fin);
        
        byte[] csvData = reporteService.exportarReporteRendimientoCSV(fecha_inicio, fecha_fin);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "rendimiento_reporte.csv");
        
        log.info("Archivo CSV de rendimiento generado exitosamente");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

    // =================== DASHBOARD Y MÉTRICAS ===================

    @Operation(
        summary = "Obtener datos del dashboard",
        description = "Recupera todos los datos necesarios para el dashboard principal"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Datos del dashboard obtenidos exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/dashboard")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERADOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Object>> obtenerDatosDashboard() {
        
        log.info("Obteniendo datos del dashboard principal");
        
        Map<String, Object> datosDashboard = reporteService.obtenerDatosDashboard();
        
        log.info("Datos del dashboard obtenidos exitosamente");
        
        return ResponseEntity.ok(datosDashboard);
    }

    @Operation(
        summary = "Obtener métricas en tiempo real",
        description = "Recupera métricas actualizadas del sistema en tiempo real"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Métricas en tiempo real obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/metricas/tiempo-real")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERADOR', 'CONSULTA')")
    public ResponseEntity<Map<String, Object>> obtenerMetricasTiempoReal() {
        
        log.debug("Obteniendo métricas en tiempo real");
        
        Map<String, Object> metricas = reporteService.obtenerMetricasTiempoReal();
        
        return ResponseEntity.ok(metricas);
    }

    @Operation(
        summary = "Obtener top áreas por reclamaciones",
        description = "Recupera las áreas con mayor cantidad de reclamaciones"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Top áreas obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/top-areas")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CONSULTA')")
    public ResponseEntity<List<Map<String, Object>>> obtenerTopAreasPorReclamaciones(
            @Parameter(description = "Cantidad máxima de resultados", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limite) {
        
        log.info("Obteniendo top {} áreas por reclamaciones", limite);
        
        List<Map<String, Object>> topAreas = reporteService.obtenerTopAreasPorReclamaciones(limite);
        
        return ResponseEntity.ok(topAreas);
    }

    @Operation(
        summary = "Obtener alertas de gestión",
        description = "Recupera alertas importantes como vencimientos, sobrecargas, etc."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Alertas obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/alertas")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERADOR')")
    public ResponseEntity<List<Map<String, Object>>> obtenerAlertasGestion() {
        
        log.info("Obteniendo alertas de gestión");
        
        List<Map<String, Object>> alertas = reporteService.obtenerAlertasGestion();
        
        log.info("Alertas de gestión obtenidas: {} alertas activas", alertas.size());
        
        return ResponseEntity.ok(alertas);
    }

    // =================== ENDPOINTS ADICIONALES ÚTILES ===================

    @Operation(
        summary = "Obtener resumen ejecutivo",
        description = "Genera un resumen ejecutivo con las métricas más importantes"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Resumen ejecutivo generado exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/resumen-ejecutivo")
    // @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> obtenerResumenEjecutivo(
            @Parameter(description = "Fecha de inicio (opcional)", example = "2024-01-01T00:00:00")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_inicio,
            
            @Parameter(description = "Fecha de fin (opcional)", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_fin) {
        
        log.info("Generando resumen ejecutivo");
        
        // Combinar múltiples fuentes de datos para el resumen
        EstadisticasDTO estadisticas = fecha_inicio != null && fecha_fin != null
            ? reporteService.obtenerEstadisticasPorPeriodo(fecha_inicio, fecha_fin)
            : reporteService.obtenerEstadisticasGenerales();
            
        Map<String, Object> alertas = Map.of("alertas", reporteService.obtenerAlertasGestion());
        Map<String, Object> topAreas = Map.of("topAreas", reporteService.obtenerTopAreasPorReclamaciones(5));
        
        Map<String, Object> resumen = Map.of(
            "estadisticas", estadisticas,
            "alertas", alertas,
            "topAreas", topAreas,
            "fechaGeneracion", LocalDateTime.now()
        );
        
        log.info("Resumen ejecutivo generado exitosamente");
        
        return ResponseEntity.ok(resumen);
    }
}
