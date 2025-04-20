package studio.devbyjose.healthyme_notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_notification.entity.EventoPendiente;
import studio.devbyjose.healthyme_notification.repository.EventoPendienteRepository;
import studio.devbyjose.healthyme_notification.service.interfaces.EventPublisherService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {

    private final EventoPendienteRepository eventoPendienteRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publicarEventoFallido(Object evento, String tipo, String tipoEvento, Integer id, String destinatario, String error) {
        try {
            String datosEvento = objectMapper.writeValueAsString(evento);

            EventoPendiente eventoPendiente = EventoPendiente.builder()
                    .tipo(tipo)
                    .tipoEvento(tipoEvento)
                    .idEntidad(id)
                    .destinatario(destinatario)
                    .fechaCreacion(LocalDateTime.now())
                    .fechaProximoIntento(LocalDateTime.now().plusMinutes(5))
                    .intentos(0)
                    .datosEvento(datosEvento)
                    .mensajeError(error)
                    .procesado(false)
                    .build();

            eventoPendienteRepository.save(eventoPendiente);
            log.info("Evento fallido guardado para procesamiento posterior: tipo={}, id={}", tipo, id);

        } catch (Exception e) {
            log.error("Error al guardar evento fallido: {}", e.getMessage(), e);
        }
    }
}