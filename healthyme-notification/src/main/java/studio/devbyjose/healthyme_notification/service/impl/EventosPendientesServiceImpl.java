package studio.devbyjose.healthyme_notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_notification.entity.EventoPendiente;
import studio.devbyjose.healthyme_notification.event.CitaEvent;
import studio.devbyjose.healthyme_notification.event.ExamenEvent;
import studio.devbyjose.healthyme_notification.event.RecetaEvent;
import studio.devbyjose.healthyme_notification.repository.EventoPendienteRepository;
import studio.devbyjose.healthyme_notification.service.interfaces.EventosPendientesService;
import studio.devbyjose.healthyme_notification.service.interfaces.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventosPendientesServiceImpl implements EventosPendientesService {

    private final EventoPendienteRepository eventoPendienteRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Override
    public void guardarEventoFallido(Object evento, String tipo, String tipoEvento, Integer id, String destinatario, String error) {
        try {
            // Serializar el evento a JSON para almacenamiento
            String datosEvento = objectMapper.writeValueAsString(evento);

            // Cálculo del tiempo de espera usando backoff exponencial:
            // - Primer intento: 5 minutos
            // - Segundo intento: 15 minutos
            // - Tercer intento: 45 minutos
            // - Cuarto intento: 2 horas
            // - Intentos posteriores: 6 horas

            EventoPendiente eventoPendiente = EventoPendiente.builder()
                    .tipo(tipo)
                    .tipoEvento(tipoEvento)
                    .idEntidad(id)
                    .destinatario(destinatario)
                    .fechaCreacion(LocalDateTime.now())
                    .fechaProximoIntento(LocalDateTime.now().plusMinutes(5)) // Primer intento en 5 minutos
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

    @Scheduled(fixedRate = 300000) // Ejecutar cada 5 minutos
    @Override
    public void procesarEventosPendientes() {
        log.info("Iniciando procesamiento de eventos pendientes");
        List<EventoPendiente> eventosPendientes = eventoPendienteRepository
                .findByProcesadoFalseAndFechaProximoIntentoLessThan(LocalDateTime.now());

        log.info("Encontrados {} eventos pendientes para procesar", eventosPendientes.size());

        for (EventoPendiente evento : eventosPendientes) {
            try {
                procesarEvento(evento);
                evento.setProcesado(true);
                eventoPendienteRepository.save(evento);
                log.info("Evento procesado exitosamente: tipo={}, id={}", evento.getTipo(), evento.getIdEntidad());
            } catch (Exception e) {
                manejarErrorReintento(evento, e);
                log.warn("Error al procesar evento pendiente: tipo={}, id={}, error={}",
                        evento.getTipo(), evento.getIdEntidad(), e.getMessage());
            }
        }
    }

    private void procesarEvento(EventoPendiente evento) throws Exception {
        if (evento.getDatosEvento() == null || evento.getDatosEvento().isEmpty()) {
            throw new IllegalArgumentException("Datos del evento están vacíos para el tipo: " + evento.getTipo());
        }

        switch (evento.getTipo()) {
            case "CITA":
                CitaEvent citaEvent = objectMapper.readValue(evento.getDatosEvento(), CitaEvent.class);
                switch (evento.getTipoEvento()) {
                    case "CREACION":
                        notificationService.enviarConfirmacionCita(citaEvent);
                        break;
                    case "ACTUALIZACION":
                        notificationService.enviarActualizacionCita(citaEvent);
                        break;
                    case "CANCELACION":
                        notificationService.enviarCancelacionCita(citaEvent);
                        break;
                    case "RECORDATORIO":
                        notificationService.enviarRecordatorioCita(citaEvent);
                        break;
                }
                break;

            case "RECETA":
                RecetaEvent recetaEvent = objectMapper.readValue(evento.getDatosEvento(), RecetaEvent.class);
                notificationService.enviarReceta(recetaEvent);
                break;

            case "EXAMEN":
                ExamenEvent examenEvent = objectMapper.readValue(evento.getDatosEvento(), ExamenEvent.class);
                notificationService.enviarResultadoExamen(examenEvent);
                break;

            default:
                log.warn("Tipo de evento desconocido: {}", evento.getTipo());
        }
    }

    private void manejarErrorReintento(EventoPendiente evento, Exception e) {
        evento.setIntentos(evento.getIntentos() + 1);
        evento.setMensajeError(e.getMessage());

        // Implementar backoff exponencial
        LocalDateTime proximoIntento;
        if (evento.getIntentos() == 1) {
            proximoIntento = LocalDateTime.now().plusMinutes(15); // 15 minutos
        } else if (evento.getIntentos() == 2) {
            proximoIntento = LocalDateTime.now().plusMinutes(45); // 45 minutos
        } else if (evento.getIntentos() == 3) {
            proximoIntento = LocalDateTime.now().plusHours(2); // 2 horas
        } else if (evento.getIntentos() < 10) {  // Máximo 10 intentos
            proximoIntento = LocalDateTime.now().plusHours(6); // 6 horas
        } else {
            proximoIntento = LocalDateTime.now().plusDays(1); // Último intento después de un día
            log.error("Evento alcanzó el límite de reintentos: tipo={}, id={}", evento.getTipo(), evento.getIdEntidad());
        }

        evento.setFechaProximoIntento(proximoIntento);
        eventoPendienteRepository.save(evento);
    }

    @Scheduled(cron = "0 0 1 * * ?") // Ejecutar a la 1am todos los días
    @Override
    public void limpiarEventosCompletados(int diasAntiguedad) {
        try {
            LocalDateTime fechaLimite = LocalDateTime.now().minusDays(diasAntiguedad);
            int eliminados = eventoPendienteRepository.deleteByProcesadoTrueAndFechaCreacionBefore(fechaLimite);
            log.info("Limpieza completada: {} eventos eliminados con más de {} días de antigüedad", eliminados, diasAntiguedad);
        } catch (Exception e) {
            log.error("Error al limpiar eventos antiguos: {}", e.getMessage(), e);
        }
    }
}