package studio.devbyjose.healthyme_notification.service.impl;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import studio.devbyjose.healthyme_notification.service.interfaces.FacturaPdfService;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturaPdfServiceImpl implements FacturaPdfService {

    private final TemplateEngine templateEngine;

    @Override
    public byte[] generarFacturaPdf(Map<String, Object> datosFactura) {
        try {
            log.info("Generando PDF de factura con datos: {}", datosFactura.keySet());
            
            // Crear contexto de Thymeleaf
            Context context = new Context();
            
            // Añadir todas las variables al contexto
            datosFactura.forEach(context::setVariable);
            
            // Procesar la plantilla HTML para PDF
            String htmlContent = templateEngine.process("factura-pdf", context);
            
            // Configurar propiedades del convertidor
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setCharset("UTF-8");
            
            // Convertir HTML a PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream, converterProperties);
            
            byte[] pdfBytes = outputStream.toByteArray();
            log.info("PDF de factura generado exitosamente. Tamaño: {} bytes", pdfBytes.length);
            
            return pdfBytes;
            
        } catch (Exception e) {
            log.error("Error al generar PDF de factura: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar PDF de factura: " + e.getMessage(), e);
        }
    }
}