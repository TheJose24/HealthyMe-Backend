package studio.devbyjose.healthyme_notification.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import studio.devbyjose.healthyme_notification.dto.AdjuntoDTO;
import studio.devbyjose.healthyme_notification.exception.NotificationException;
import studio.devbyjose.healthyme_notification.service.interfaces.PdfGenerationService;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationServiceImpl implements PdfGenerationService {

    private final SpringTemplateEngine templateEngine;

    @Override
    @CircuitBreaker(name = "pdfGenerationService", fallbackMethod = "generarRecetaPdfFallback")
    public AdjuntoDTO generarRecetaPdf(Map<String, Object> datos) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Agregar encabezado
            Paragraph header = new Paragraph("HealthyMe - Receta Médica",
                    new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(Chunk.NEWLINE);

            // Datos del médico
            document.add(new Paragraph("Médico: " + datos.getOrDefault("nombreMedico", ""),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(new Paragraph("Especialidad: " + datos.getOrDefault("especialidad", ""),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(new Paragraph("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(Chunk.NEWLINE);

            // Datos del paciente
            document.add(new Paragraph("Paciente: " + datos.getOrDefault("nombrePaciente", ""),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(Chunk.NEWLINE);

            // Receta
            document.add(new Paragraph("INDICACIONES:",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            document.add(new Paragraph((String) datos.getOrDefault("indicaciones", ""),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(Chunk.NEWLINE);

            // Medicamentos
            document.add(new Paragraph("MEDICAMENTOS:",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Añadir encabezados
            PdfPCell cell1 = new PdfPCell(new Phrase("Medicamento",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            PdfPCell cell2 = new PdfPCell(new Phrase("Dosis",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            PdfPCell cell3 = new PdfPCell(new Phrase("Indicaciones",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

            cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);

            // Datos de medicamentos (esto debería venir en el mapa 'datos')
            if (datos.containsKey("medicamentos") && datos.get("medicamentos") instanceof java.util.List<?> medicamentos) {

                for (Object med : medicamentos) {
                    if (med instanceof Map) {
                        Map<String, String> medicamento = (Map<String, String>) med;
                        table.addCell(medicamento.getOrDefault("nombre", ""));
                        table.addCell(medicamento.getOrDefault("dosis", ""));
                        table.addCell(medicamento.getOrDefault("indicaciones", ""));
                    }
                }
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Firma
            document.add(new Paragraph("______________________________",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(new Paragraph("Firma del Médico",
                    new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)));

            document.close();

            return AdjuntoDTO.builder()
                    .nombre("receta_medica.pdf")
                    .tipoContenido("application/pdf")
                    .contenido(outputStream.toByteArray())
                    .build();

        } catch (Exception e) {
            log.error("Error al generar PDF de receta: {}", e.getMessage(), e);
            throw new NotificationException("Error al generar PDF de receta: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @CircuitBreaker(name = "pdfGenerationService", fallbackMethod = "generarResultadoLaboratorioPdfFallback")
    public AdjuntoDTO generarResultadoLaboratorioPdf(Map<String, Object> datos) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Agregar encabezado
            Paragraph header = new Paragraph("HealthyMe - Resultados de Laboratorio",
                    new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(Chunk.NEWLINE);

            // Datos del laboratorio
            document.add(new Paragraph("Laboratorio: HealthyMe Lab",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(new Paragraph("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(Chunk.NEWLINE);

            // Datos del paciente
            document.add(new Paragraph("Paciente: " + datos.getOrDefault("nombrePaciente", ""),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(new Paragraph("Examen: " + datos.getOrDefault("tipoExamen", ""),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(Chunk.NEWLINE);

            // Resultados
            document.add(new Paragraph("RESULTADOS:",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Añadir encabezados
            PdfPCell cell1 = new PdfPCell(new Phrase("Parámetro",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            PdfPCell cell2 = new PdfPCell(new Phrase("Resultado",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            PdfPCell cell3 = new PdfPCell(new Phrase("Valores de Referencia",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

            cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);

            // Datos de resultados (esto debería venir en el mapa 'datos')
            if (datos.containsKey("resultados") && datos.get("resultados") instanceof java.util.List<?> resultados) {

                for (Object res : resultados) {
                    if (res instanceof Map) {
                        Map<String, String> resultado = (Map<String, String>) res;
                        table.addCell(resultado.getOrDefault("parametro", ""));
                        table.addCell(resultado.getOrDefault("valor", ""));
                        table.addCell(resultado.getOrDefault("referencia", ""));
                    }
                }
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Observaciones
            if (datos.containsKey("observaciones")) {
                document.add(new Paragraph("OBSERVACIONES:",
                        new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
                document.add(new Paragraph(datos.get("observaciones").toString(),
                        new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
                document.add(Chunk.NEWLINE);
            }

            // Firma
            document.add(new Paragraph("______________________________",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            document.add(new Paragraph("Técnico de Laboratorio",
                    new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)));

            document.close();

            return AdjuntoDTO.builder()
                    .nombre("resultados_laboratorio.pdf")
                    .tipoContenido("application/pdf")
                    .contenido(outputStream.toByteArray())
                    .build();

        } catch (Exception e) {
            log.error("Error al generar PDF de resultados: {}", e.getMessage(), e);
            throw new NotificationException("Error al generar PDF de resultados: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public AdjuntoDTO generarPdfPersonalizado(String nombrePlantilla, Map<String, Object> datos) {
        // Primero generamos el contenido HTML con Thymeleaf
        Context context = new Context();
        if (datos != null) {
            datos.forEach(context::setVariable);
        }

        String html = templateEngine.process(nombrePlantilla, context);

        // Aquí podrías usar una biblioteca que convierta HTML a PDF
        // Por simplicidad, creamos un PDF básico
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("Este es un documento PDF personalizado",
                    new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
            document.add(Chunk.NEWLINE);

            // Simple implementación que añade las variables como texto
            assert datos != null;
            for (Map.Entry<String, Object> entry : datos.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": " + entry.getValue(),
                        new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
            }

            document.close();

            return AdjuntoDTO.builder()
                    .nombre("documento_personalizado.pdf")
                    .tipoContenido("application/pdf")
                    .contenido(outputStream.toByteArray())
                    .build();

        } catch (Exception e) {
            log.error("Error al generar PDF personalizado: {}", e.getMessage(), e);
            throw new NotificationException("Error al generar PDF personalizado: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Metodo utilitario para generar un PDF simple con mensaje de error
    private byte[] generarPdfSimpleDeError(String mensaje) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        com.itextpdf.text.pdf.PdfWriter.getInstance(document, baos);

        document.open();
        document.add(new com.itextpdf.text.Paragraph(mensaje));
        document.add(new com.itextpdf.text.Paragraph("Por favor, intente más tarde."));
        document.close();

        return baos.toByteArray();
    }

    public AdjuntoDTO generarRecetaPdfFallback(Map<String, Object> datosReceta, Exception ex) {
        log.error("Circuit breaker activado para generarRecetaPdf: {}", ex.getMessage());

        // Crear un PDF simple de "error" que muestre que el servicio no está disponible
        try {
            // Generar un PDF muy sencillo
            byte[] pdfSimple = generarPdfSimpleDeError("Receta médica temporalmente no disponible");

            return AdjuntoDTO.builder()
                    .nombre("receta_no_disponible.pdf")
                    .tipoContenido("application/pdf")
                    .contenido(pdfSimple)
                    .build();
        } catch (Exception e) {
            log.error("Error al generar PDF de fallback: {}", e.getMessage());
            return null;
        }
    }

    public AdjuntoDTO generarResultadoLaboratorioPdfFallback(Map<String, Object> datosExamen, Exception ex) {
        log.error("Circuit breaker activado para generarResultadoLaboratorioPdf: {}", ex.getMessage());

        try {
            byte[] pdfSimple = generarPdfSimpleDeError("Resultados de laboratorio temporalmente no disponibles");

            return AdjuntoDTO.builder()
                    .nombre("resultados_no_disponibles.pdf")
                    .tipoContenido("application/pdf")
                    .contenido(pdfSimple)
                    .build();
        } catch (Exception e) {
            log.error("Error al generar PDF de fallback: {}", e.getMessage());
            return null;
        }
    }
}