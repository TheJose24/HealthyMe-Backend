package studio.devbyjose.healthyme_notification.service.interfaces;

import studio.devbyjose.healthyme_notification.dto.AdjuntoDTO;

import java.util.List;
import java.util.Map;

public interface EmailService {

    /**
     * Envía un email utilizando una plantilla
     *
     * @param destinatario Dirección de correo electrónico del destinatario
     * @param asunto Asunto del correo
     * @param nombrePlantilla Nombre de la plantilla a utilizar
     * @param model Variables para la plantilla
     */
    void enviarEmail(String destinatario, String asunto, String nombrePlantilla, Map<String, Object> model);

    /**
     * Envía un email con adjuntos utilizando una plantilla
     *
     * @param destinatario Dirección de correo electrónico del destinatario
     * @param asunto Asunto del correo
     * @param nombrePlantilla Nombre de la plantilla a utilizar
     * @param model Variables para la plantilla
     * @param adjuntos Lista de adjuntos
     */
    void enviarEmailConAdjuntos(String destinatario, String asunto, String nombrePlantilla,
                                Map<String, Object> model, List<AdjuntoDTO> adjuntos);
}