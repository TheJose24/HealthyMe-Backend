package studio.devbyjose.healthyme_payment.service.interfaces;

import studio.devbyjose.healthyme_payment.dto.*;

import java.time.LocalDate;

public interface ReporteService {

    ReporteGeneralDTO getReporteGeneral();

    ReporteMensualDTO getReporteMensual(LocalDate fecha);

    ReporteAnualDTO getReporteAnual(int year);

    CitasReporteDTO getEstadisticasCitas(LocalDate fechaInicio, LocalDate fechaFin);

    BalanceMensualDTO getIngresosPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

    Long getMedicosActivos();

    byte[] generatePdfReporteGeneral();

    byte[] generatePdfReporteMensual(LocalDate fecha);

    byte[] generatePdfReporteAnual(int year);

    byte[] generatePdfReporteCompleto(LocalDate fechaInicio, LocalDate fechaFin);
}
