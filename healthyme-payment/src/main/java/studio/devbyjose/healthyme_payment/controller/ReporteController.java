package studio.devbyjose.healthyme_payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_payment.dto.*;
import studio.devbyjose.healthyme_payment.service.interfaces.ReporteService;

import java.time.LocalDate;

@Tag(name = "Reportes", description = "API para generar reportes del sistema")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Reporte general del sistema")
    @GetMapping("/general")
    public ResponseEntity<ReporteGeneralDTO> getReporteGeneral() {
        return ResponseEntity.ok(reporteService.getReporteGeneral());
    }

    @Operation(summary = "Reporte mensual")
    @GetMapping("/mensual")
    public ResponseEntity<ReporteMensualDTO> getReporteMensual(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(reporteService.getReporteMensual(fecha));
    }

    @Operation(summary = "Reporte anual")
    @GetMapping("/anual")
    public ResponseEntity<ReporteAnualDTO> getReporteAnual(
            @RequestParam int year) {
        return ResponseEntity.ok(reporteService.getReporteAnual(year));
    }

    @Operation(summary = "Estadísticas de citas")
    @GetMapping("/citas")
    public ResponseEntity<CitasReporteDTO> getEstadisticasCitas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reporteService.getEstadisticasCitas(fechaInicio, fechaFin));
    }

    @Operation(summary = "Ingresos por período")
    @GetMapping("/ingresos")
    public ResponseEntity<BalanceMensualDTO> getIngresosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reporteService.getIngresosPorPeriodo(fechaInicio, fechaFin));
    }

    @Operation(summary = "Médicos activos")
    @GetMapping("/medicos-activos")
    public ResponseEntity<Long> getMedicosActivos() {
        return ResponseEntity.ok(reporteService.getMedicosActivos());
    }

    @Operation(summary = "Generar PDF reporte general")
    @GetMapping(value = "/pdf/general", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdfReporteGeneral() {
        byte[] pdfBytes = reporteService.generatePdfReporteGeneral();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-general.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @Operation(summary = "Generar PDF reporte completo")
    @GetMapping(value = "/pdf/completo", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdfReporteCompleto(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        byte[] pdfBytes = reporteService.generatePdfReporteCompleto(fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                String.format("reporte-completo-%s-a-%s.pdf", fechaInicio, fechaFin));

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}