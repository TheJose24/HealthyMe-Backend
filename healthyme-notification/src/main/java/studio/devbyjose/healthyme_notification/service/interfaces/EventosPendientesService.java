package studio.devbyjose.healthyme_notification.service.interfaces;

import studio.devbyjose.healthyme_notification.event.CitaEvent;
import studio.devbyjose.healthyme_notification.event.ConsultaEvent;
import studio.devbyjose.healthyme_notification.event.ExamenEvent;
import studio.devbyjose.healthyme_notification.event.RecetaEvent;

public interface EventosPendientesService {
    void guardarEventoFallido(Object evento, String tipo, String tipoEvento, Integer id, String destinatario, String error);
    void procesarEventosPendientes();
    void limpiarEventosCompletados(int diasAntiguedad);
}