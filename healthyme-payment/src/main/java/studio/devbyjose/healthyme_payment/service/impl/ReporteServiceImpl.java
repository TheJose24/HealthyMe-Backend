package studio.devbyjose.healthyme_payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_commons.client.dto.CitasPorDiaDTO;
import studio.devbyjose.healthyme_commons.client.dto.EspecialidadContadaDTO;
import studio.devbyjose.healthyme_commons.client.feign.*;
import studio.devbyjose.healthyme_commons.enums.citas.EstadoCita;
import studio.devbyjose.healthyme_payment.dto.*;
import studio.devbyjose.healthyme_payment.repository.*;
import studio.devbyjose.healthyme_payment.service.interfaces.PdfGenerationService;
import studio.devbyjose.healthyme_payment.service.interfaces.ReporteService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    // 🔗 Clientes Feign existentes
    private final CitaClient citaClient;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;

    // 💾 Repositorios existentes
    private final PagoRepository pagoRepository;

    // Dependencias existentes...
    private final PdfGenerationService pdfGenerationService;

    @Override
    public ReporteGeneralDTO getReporteGeneral() {
        log.info("Generando reporte general del sistema");

        return ReporteGeneralDTO.builder()
                .fechaGeneracion(LocalDateTime.now())
                .totalCitas(getTotalCitasSafe())
                .citasPendientes(getCitasByEstadoSafe(EstadoCita.PENDIENTE))
                .citasRealizadas(getCitasByEstadoSafe(EstadoCita.REALIZADA))
                .ingresosTotales(getTotalIngresos())
                .ingresosMesActual(getIngresosMesActual())
                .medicosActivos(getMedicosActivos())
                .pacientesTotales(getTotalPacientesSafe())
                .especialidadesMasSolicitadas(getEspecialidadesMasSolicitadasSafe())
                .build();
    }

    @Override
    public ReporteMensualDTO getReporteMensual(LocalDate fecha) {
        log.info("Generando reporte mensual para: {}", fecha);

        LocalDate inicioMes = fecha.withDayOfMonth(1);
        LocalDate finMes = fecha.withDayOfMonth(fecha.lengthOfMonth());

        return ReporteMensualDTO.builder()
                .mes(fecha)
                .citasDelMes(getCitasEnRangoSafe(inicioMes, finMes))
                .ingresosDelMes(getIngresosPorPeriodo(inicioMes, finMes).getMonto())
                .medicosActivosDelMes(getMedicosActivos().intValue())
                .citasPorDia(getCitasPorDiaEnRangoSafe(inicioMes, finMes))
                .ingresosPorDia(getIngresosPorDiaEnRangoSafe(inicioMes, finMes))
                .build();
    }

    @Override
    public ReporteAnualDTO getReporteAnual(int year) {
        log.info("Generando reporte anual para el año: {}", year);

        LocalDate inicioAno = LocalDate.of(year, 1, 1);
        LocalDate finAno = LocalDate.of(year, 12, 31);

        return ReporteAnualDTO.builder()
                .year(year)
                .citasDelAno(getCitasEnRangoSafe(inicioAno, finAno))
                .ingresosDelAno(getIngresosPorPeriodo(inicioAno, finAno).getMonto())
                .build();
    }

    @Override
    public CitasReporteDTO getEstadisticasCitas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Generando estadísticas de citas desde {} hasta {}", fechaInicio, fechaFin);

        Long totalCitas = getCitasEnRangoSafe(fechaInicio, fechaFin);
        Long citasPendientes = getCitasByEstadoEnRangoSafe(EstadoCita.PENDIENTE, fechaInicio, fechaFin);
        Long citasRealizadas = getCitasByEstadoEnRangoSafe(EstadoCita.REALIZADA, fechaInicio, fechaFin);
        Long citasCanceladas = getCitasByEstadoEnRangoSafe(EstadoCita.CANCELADA, fechaInicio, fechaFin);

        double tasaCompletitud = totalCitas > 0 ?
            (citasRealizadas.doubleValue() / totalCitas.doubleValue()) * 100 : 0.0;

        return CitasReporteDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .totalCitas(totalCitas)
                .citasPendientes(citasPendientes)
                .citasRealizadas(citasRealizadas)
                .citasCanceladas(citasCanceladas)
                .tasaCompletitud(Math.round(tasaCompletitud * 100.0) / 100.0)
                .build();
    }

    @Override
    public BalanceMensualDTO getIngresosPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Calculando ingresos desde {} hasta {}", fechaInicio, fechaFin);

        BigDecimal ingresos = BigDecimal.ZERO;
        Long cantidadPagos = 0L;
        
        try {
            ingresos = pagoRepository.sumIngresosPorPeriodo(fechaInicio, fechaFin);
            cantidadPagos = pagoRepository.countPagosPorPeriodo(fechaInicio, fechaFin);
        } catch (Exception e) {
            log.warn("Error al calcular ingresos: {}", e.getMessage());
        }

        return BalanceMensualDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .monto(ingresos != null ? ingresos : BigDecimal.ZERO)
                .cantidadTransacciones(cantidadPagos != null ? cantidadPagos : 0L)
                .build();
    }

    @Override
    public Long getMedicosActivos() {
        try {
            return medicoClient.getMedicosActivos();
        } catch (Exception e) {
            log.warn("Error al obtener médicos activos, devolviendo 0: {}", e.getMessage());
            return 0L;
        }
    }

    // 📄 MÉTODOS PARA GENERAR PDFs (sin cambios)

    @Override
    public byte[] generatePdfReporteGeneral() {
        log.info("Generando PDF del reporte general");
        
        ReporteGeneralDTO reporte = getReporteGeneral();
        Map<String, Object> datos = new HashMap<>();
        datos.put("reporte", reporte);
        
        return pdfGenerationService.generarReporteGeneralPdf(datos);
    }

    @Override
    public byte[] generatePdfReporteMensual(LocalDate fecha) {
        log.info("Generando PDF del reporte mensual para: {}", fecha);
        
        ReporteMensualDTO reporte = getReporteMensual(fecha);
        Map<String, Object> datos = new HashMap<>();
        datos.put("reporte", reporte);
        
        return pdfGenerationService.generarReporteMensualPdf(datos);
    }

    @Override
    public byte[] generatePdfReporteAnual(int year) {
        return new byte[0];
    }

    @Override
    public byte[] generatePdfReporteCompleto(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Generando PDF del reporte completo desde {} hasta {}", fechaInicio, fechaFin);

        // 📊 Recopilar todos los datos
        Map<String, Object> datos = new HashMap<>();
        
        // Datos del reporte
        ReporteGeneralDTO reporteGeneral = getReporteGeneral();
        CitasReporteDTO estadisticasCitas = getEstadisticasCitas(fechaInicio, fechaFin);
        BalanceMensualDTO balanceFinanciero = getIngresosPorPeriodo(fechaInicio, fechaFin);
        
        // 🎯 Agregar al contexto
        datos.put("reporte", Map.of(
            "fechaInicio", fechaInicio,
            "fechaFin", fechaFin,
            "fechaGeneracion", LocalDateTime.now(),
            "totalCitas", reporteGeneral.getTotalCitas(),
            "ingresosTotales", reporteGeneral.getIngresosTotales(),
            "medicosActivos", reporteGeneral.getMedicosActivos(),
            "pacientesTotales", reporteGeneral.getPacientesTotales()
        ));
        
        datos.put("estadisticasCitas", estadisticasCitas);
        datos.put("balanceFinanciero", balanceFinanciero);
        datos.put("especialidades", getEspecialidadesMasSolicitadasSafe());
        datos.put("ingresosPorDia", getIngresosPorDiaEnRangoSafe(fechaInicio, fechaFin));
        
        return pdfGenerationService.generarReporteCompletoPdf(datos);
    }

    // 🔧 MÉTODOS PRIVADOS CON DATOS REALES

    private Long getTotalCitasSafe() {
        try {
            return citaClient.getTotalCitas();
        } catch (Exception e) {
            log.warn("Error al obtener total de citas: {}", e.getMessage());
            return 0L;
        }
    }

    private Long getCitasByEstadoSafe(EstadoCita estado) {
        try {
            return citaClient.getCitasByEstado(estado);
        } catch (Exception e) {
            log.warn("Error al obtener citas por estado {}: {}", estado, e.getMessage());
            return 0L;
        }
    }

    private Long getCitasEnRangoSafe(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            return citaClient.getCitasEnRango(fechaInicio, fechaFin);
        } catch (Exception e) {
            log.warn("Error al obtener citas en rango: {}", e.getMessage());
            return 0L;
        }
    }

    private Long getCitasByEstadoEnRangoSafe(EstadoCita estado, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            return citaClient.getCitasByEstadoEnRango(estado, fechaInicio, fechaFin);
        } catch (Exception e) {
            log.warn("Error al obtener citas por estado {} en rango: {}", estado, e.getMessage());
            return 0L;
        }
    }

    private List<CitasPorDiaDTO> getCitasPorDiaEnRangoSafe(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            return citaClient.getCitasPorDiaEnRango(fechaInicio, fechaFin);
        } catch (Exception e) {
            log.warn("Error al obtener citas por día: {}", e.getMessage());
            return List.of();
        }
    }

    private List<IngresosPorDiaDTO> getIngresosPorDiaEnRangoSafe(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            return pagoRepository.getIngresosPorDiaEnRango(fechaInicio, fechaFin);
        } catch (Exception e) {
            log.warn("Error al obtener ingresos por día: {}", e.getMessage());
            return List.of();
        }
    }

    private List<EspecialidadContadaDTO> getEspecialidadesMasSolicitadasSafe() {
        try {
            return citaClient.getEspecialidadesMasSolicitadas();
        } catch (Exception e) {
            log.warn("Error al obtener especialidades más solicitadas: {}", e.getMessage());
            return List.of();
        }
    }

    private Long getTotalPacientesSafe() {
        try {
            return pacienteClient.getTotalPacientes();
        } catch (Exception e) {
            log.warn("Error al obtener total de pacientes: {}", e.getMessage());
            return 0L;
        }
    }

    private BigDecimal getTotalIngresos() {
        try {
            return pagoRepository.sumTotalIngresos();
        } catch (Exception e) {
            log.warn("Error al calcular ingresos totales: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getIngresosMesActual() {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return getIngresosPorPeriodo(inicioMes, finMes).getMonto();
    }

    // Métodos para construir contenido del PDF (sin cambios)
    private String buildReporteGeneralContent(ReporteGeneralDTO reporte) {
        return String.format(
                """
                        Total Citas: %d
                        Citas Pendientes: %d
                        Citas Realizadas: %d
                        Ingresos Totales: $%s
                        Ingresos Mes Actual: $%s
                        Médicos Activos: %d
                        Total Pacientes: %d
                        """,
            reporte.getTotalCitas(),
            reporte.getCitasPendientes(),
            reporte.getCitasRealizadas(),
            reporte.getIngresosTotales(),
            reporte.getIngresosMesActual(),
            reporte.getMedicosActivos(),
            reporte.getPacientesTotales()
        );
    }

    private String buildReporteMensualContent(ReporteMensualDTO reporte) {
        return String.format(
                """
                        Mes: %s
                        Citas del Mes: %d
                        Ingresos del Mes: $%s
                        Médicos Activos: %d
                        """,
            reporte.getMes(),
            reporte.getCitasDelMes(),
            reporte.getIngresosDelMes(),
            reporte.getMedicosActivosDelMes()
        );
    }

    private String buildReporteAnualContent(ReporteAnualDTO reporte) {
        return String.format(
                """
                        Año: %d
                        Citas del Año: %d
                        Ingresos del Año: $%s
                        """,
            reporte.getYear(),
            reporte.getCitasDelAno(),
            reporte.getIngresosDelAno()
        );
    }

    private String buildCitasReporteContent(CitasReporteDTO reporte) {
        return String.format(
                """
                        Total Citas: %d
                        Pendientes: %d
                        Realizadas: %d
                        Canceladas: %d
                        Tasa de Completitud: %.1f%%
                        """,
            reporte.getTotalCitas(),
            reporte.getCitasPendientes(),
            reporte.getCitasRealizadas(),
            reporte.getCitasCanceladas(),
            reporte.getTasaCompletitud()
        );
    }

    private String buildIngresosContent(BalanceMensualDTO balance) {
        return String.format(
                """
                        Período: %s - %s
                        Monto Total: $%s
                        Cantidad Transacciones: %d
                        """,
            balance.getFechaInicio(),
            balance.getFechaFin(),
            balance.getMonto(),
            balance.getCantidadTransacciones()
        );
    }
}