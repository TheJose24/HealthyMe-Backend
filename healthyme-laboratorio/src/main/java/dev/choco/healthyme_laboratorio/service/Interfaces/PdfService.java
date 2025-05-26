package dev.choco.healthyme_laboratorio.service.Interfaces;

import dev.choco.healthyme_laboratorio.entity.Examen;

public interface PdfService {
    byte[] generarPdfExamen(Examen examen);
}
