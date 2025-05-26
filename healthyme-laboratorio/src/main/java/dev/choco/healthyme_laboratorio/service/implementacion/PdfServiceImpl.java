package dev.choco.healthyme_laboratorio.service.implementacion;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import dev.choco.healthyme_laboratorio.entity.Examen;
import dev.choco.healthyme_laboratorio.service.Interfaces.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final TemplateEngine templateEngine;

    @Override
    public byte[] generarPdfExamen(Examen examen) {
        try {
            Context context = new Context();
            context.setVariable("nombreExamen", examen.getNombreExamen());
            context.setVariable("resultados", examen.getResultados());
            context.setVariable("observaciones", examen.getObservaciones());
            context.setVariable("fechaRealizacion", examen.getFechaRealizacion());
            context.setVariable("idPaciente", examen.getIdPaciente());
            context.setVariable("idTecnico", examen.getIdTecnico());
            context.setVariable("idLaboratorio", examen.getIdLaboratorio());

            String html = templateEngine.process("examen-pdf", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ConverterProperties props = new ConverterProperties();
            props.setCharset("UTF-8");

            HtmlConverter.convertToPdf(html, outputStream, props);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF del examen", e);
        }
    }
}
