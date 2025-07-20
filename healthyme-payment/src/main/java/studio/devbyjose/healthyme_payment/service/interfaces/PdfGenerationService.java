package studio.devbyjose.healthyme_payment.service.interfaces;

import java.util.Map;

public interface PdfGenerationService {

    byte[] generarReporteCompletoPdf(Map<String, Object> datos);

    byte[] generarReporteGeneralPdf(Map<String, Object> datos);

    byte[] generarReporteMensualPdf(Map<String, Object> datos);
}
