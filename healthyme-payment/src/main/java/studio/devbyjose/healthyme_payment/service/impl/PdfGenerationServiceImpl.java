package studio.devbyjose.healthyme_payment.service.impl;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import studio.devbyjose.healthyme_payment.service.interfaces.PdfGenerationService;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationServiceImpl implements PdfGenerationService {

    private final TemplateEngine templateEngine;

    @Override
    public byte[] generarReporteCompletoPdf(Map<String, Object> datos) {
        try {
            log.info("Generando PDF de reporte completo");

            // 🎯 Crear contexto de Thymeleaf
            Context context = new Context();
            datos.forEach(context::setVariable);

            // 📝 Procesar plantilla HTML
            String htmlContent = templateEngine.process("reporte-completo-pdf", context);

            // ⚙️ Configurar propiedades de conversión
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setCharset("UTF-8");

            // 🔄 Convertir HTML a PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream, converterProperties);

            byte[] pdfBytes = outputStream.toByteArray();
            log.info("PDF generado exitosamente. Tamaño: {} bytes", pdfBytes.length);

            return pdfBytes;

        } catch (Exception e) {
            log.error("Error al generar PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generarReporteGeneralPdf(Map<String, Object> datos) {
        try {
            Context context = new Context();
            datos.forEach(context::setVariable);

            String htmlContent = templateEngine.process("reporte-general", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ConverterProperties properties = new ConverterProperties();
            properties.setCharset("UTF-8");

            HtmlConverter.convertToPdf(htmlContent, outputStream, properties);
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar PDF reporte general: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generarReporteMensualPdf(Map<String, Object> datos) {
        try {
            Context context = new Context();
            datos.forEach(context::setVariable);

            String htmlContent = templateEngine.process("reporte-mensual", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ConverterProperties properties = new ConverterProperties();
            properties.setCharset("UTF-8");

            HtmlConverter.convertToPdf(htmlContent, outputStream, properties);
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar PDF reporte mensual: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }
}