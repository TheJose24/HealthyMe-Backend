package dev.Elmer.healthyme_consultas.service.interfaces;

import dev.Elmer.healthyme_consultas.entity.Receta;

public interface PdfService {
    byte[] generarPdfExamen(Receta receta);
}