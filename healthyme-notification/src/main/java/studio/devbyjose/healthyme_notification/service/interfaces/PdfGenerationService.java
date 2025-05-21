package studio.devbyjose.healthyme_notification.service.interfaces;

import studio.devbyjose.healthyme_commons.client.dto.AdjuntoDTO;

import java.util.Map;

public interface PdfGenerationService {

    /**
     * Genera un PDF de una receta médica
     *
     * @param datos Datos para la generación del PDF
     * @return AdjuntoDTO con el contenido del PDF generado
     */
    AdjuntoDTO generarRecetaPdf(Map<String, Object> datos);

    /**
     * Genera un PDF con resultados de laboratorio
     *
     * @param datos Datos para la generación del PDF
     * @return AdjuntoDTO con el contenido del PDF generado
     */
    AdjuntoDTO generarResultadoLaboratorioPdf(Map<String, Object> datos);

    /**
     * Genera un PDF personalizado basado en una plantilla
     *
     * @param nombrePlantilla Nombre de la plantilla a utilizar
     * @param datos Datos para la generación del PDF
     * @return AdjuntoDTO con el contenido del PDF generado
     */
    AdjuntoDTO generarPdfPersonalizado(String nombrePlantilla, Map<String, Object> datos);
}