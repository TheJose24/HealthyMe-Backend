package dev.Elmer.healthyme_consultas.service.implementacion;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import dev.Elmer.healthyme_consultas.entity.Receta;
import dev.Elmer.healthyme_consultas.service.interfaces.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor

public class PdfServiceImpl implements PdfService{

    private final TemplateEngine templateEngine;

    @Override
    public byte[] generarPdfExamen(Receta receta) {
        try {
            Context context = new Context();
            context.setVariable("receta", receta);
            context.setVariable("consulta", receta.getConsulta());
            context.setVariable("medicamentos", receta.getMedicamentos());
            String html = templateEngine.process("receta-pdf", context);

            ConverterProperties props = new ConverterProperties();
            props.setCharset("UTF-8");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(html, outputStream, props);

            return outputStream.toByteArray();


        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF d la receta", e);
        }
    }
}