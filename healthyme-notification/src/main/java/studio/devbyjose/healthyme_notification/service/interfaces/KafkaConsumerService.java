package studio.devbyjose.healthyme_notification.service.interfaces;

import studio.devbyjose.healthyme_notification.event.CitaEvent;
import studio.devbyjose.healthyme_notification.event.ConsultaEvent;
import studio.devbyjose.healthyme_notification.event.ExamenEvent;
import studio.devbyjose.healthyme_notification.event.RecetaEvent;

public interface KafkaConsumerService {
    void consumeCitaEvent(CitaEvent citaEvent);
    void consumeRecetaEvent(RecetaEvent recetaEvent);
    void consumeExamenEvent(ExamenEvent examenEvent);
}