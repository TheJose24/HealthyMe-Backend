package studio.devbyjose.healthyme_notification.service.interfaces;

public interface EventPublisherService {
    void publicarEventoFallido(Object evento, String tipo, String tipoEvento, Integer id, String destinatario, String error);
}