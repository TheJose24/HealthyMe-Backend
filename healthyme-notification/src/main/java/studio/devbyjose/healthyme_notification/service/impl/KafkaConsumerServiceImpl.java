package studio.devbyjose.healthyme_notification.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_notification.event.CitaEvent;
import studio.devbyjose.healthyme_notification.event.ConsultaEvent;
import studio.devbyjose.healthyme_notification.event.ExamenEvent;
import studio.devbyjose.healthyme_notification.event.RecetaEvent;
import studio.devbyjose.healthyme_notification.service.interfaces.EventosPendientesService;
import studio.devbyjose.healthyme_notification.service.interfaces.KafkaConsumerService;
import studio.devbyjose.healthyme_notification.service.interfaces.NotificationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final NotificationService notificationService;
    private final EventosPendientesService eventosPendientesService;

    @Override
    @KafkaListener(topics = "citas-events", groupId = "${spring.kafka.consumer.group-id}")
    @CircuitBreaker(name = "kafkaConsumer", fallbackMethod = "consumeCitaEventFallback")
    public void consumeCitaEvent(CitaEvent citaEvent) {
        log.info("Recibido evento de cita: {}", citaEvent);

        try {
            switch (citaEvent.getTipoEvento()) {
                case CREACION:
                    notificationService.enviarConfirmacionCita(citaEvent);
                    break;
                case ACTUALIZACION:
                    notificationService.enviarActualizacionCita(citaEvent);
                    break;
                case CANCELACION:
                    notificationService.enviarCancelacionCita(citaEvent);
                    break;
                case RECORDATORIO:
                    notificationService.enviarRecordatorioCita(citaEvent);
                    break;
                default:
                    log.warn("Tipo de evento de cita no soportado: {}", citaEvent.getTipoEvento());
            }
        } catch (Exception e) {
            log.error("Error al procesar evento de cita: {}", e.getMessage(), e);
        }
    }

    @Override
    @KafkaListener(topics = "recetas-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRecetaEvent(RecetaEvent recetaEvent) {
        log.info("Recibido evento de receta: {}", recetaEvent);

        try {
            notificationService.enviarReceta(recetaEvent);
        } catch (Exception e) {
            log.error("Error al procesar evento de receta: {}", e.getMessage(), e);
        }
    }

    @Override
    @KafkaListener(topics = "examenes-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeExamenEvent(ExamenEvent examenEvent) {
        log.info("Recibido evento de examen: {}", examenEvent);

        try {
            if (examenEvent.getTipoEvento() == ExamenEvent.TipoEventoExamen.RESULTADO_DISPONIBLE) {
                notificationService.enviarResultadoExamen(examenEvent);
            }
        } catch (Exception e) {
            log.error("Error al procesar evento de examen: {}", e.getMessage(), e);
        }
    }

    public void consumeCitaEventFallback(CitaEvent citaEvent, Exception ex) {
        log.error("Circuit breaker activado para evento de cita: {}", ex.getMessage());

        // Guardar el evento para procesamiento posterior
        eventosPendientesService.guardarEventoFallido(
                citaEvent,
                "CITA",
                citaEvent.getTipoEvento().name(),
                citaEvent.getIdCita(),
                citaEvent.getEmailPaciente(),
                ex.getMessage()
        );
    }
}