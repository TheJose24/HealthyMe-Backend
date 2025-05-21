package studio.devbyjose.healthyme_notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studio.devbyjose.healthyme_commons.client.dto.AdjuntoDTO;
import studio.devbyjose.healthyme_commons.client.dto.NotificacionDTO;
import studio.devbyjose.healthyme_commons.client.dto.PacienteDTO;
import studio.devbyjose.healthyme_commons.client.feign.MedicoClient;
import studio.devbyjose.healthyme_commons.client.feign.PacienteClient;
import studio.devbyjose.healthyme_commons.client.feign.RecetaClient;
import studio.devbyjose.healthyme_commons.exception.ResourceNotFoundException;
import studio.devbyjose.healthyme_notification.dto.PlantillaDTO;
import studio.devbyjose.healthyme_notification.entity.Adjunto;
import studio.devbyjose.healthyme_notification.entity.Notificacion;
import studio.devbyjose.healthyme_notification.entity.Plantilla;
import studio.devbyjose.healthyme_commons.enums.notification.EstadoNotificacion;
import studio.devbyjose.healthyme_notification.event.CitaEvent;
import studio.devbyjose.healthyme_notification.event.ExamenEvent;
import studio.devbyjose.healthyme_notification.event.RecetaEvent;
import studio.devbyjose.healthyme_notification.exception.NotificationException;
import studio.devbyjose.healthyme_notification.mapper.AdjuntoMapper;
import studio.devbyjose.healthyme_notification.mapper.NotificacionMapper;
import studio.devbyjose.healthyme_notification.mapper.PlantillaMapper;
import studio.devbyjose.healthyme_notification.repository.AdjuntoRepository;
import studio.devbyjose.healthyme_notification.repository.NotificacionRepository;
import studio.devbyjose.healthyme_notification.repository.PlantillaRepository;
import studio.devbyjose.healthyme_notification.service.interfaces.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;
    private final PdfGenerationService pdfGenerationService;
    private final NotificacionRepository notificacionRepository;
    private final PlantillaRepository plantillaRepository;
    private final AdjuntoRepository adjuntoRepository;
    private final ObjectMapper objectMapper;
    private final EventPublisherService eventPublisherService;

    // Clientes Feign
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;
    private final RecetaClient recetaClient;

    // Mappers
    private final NotificacionMapper notificacionMapper;
    private final PlantillaMapper plantillaMapper;
    private final AdjuntoMapper adjuntoMapper;

    @Override
    @CircuitBreaker(name = "emailService", fallbackMethod = "enviarNotificacionFallback")
    @Transactional
    public void enviarNotificacion(NotificacionDTO notificacionDTO) {
        try {
            // Buscar la plantilla
            Plantilla plantilla = plantillaRepository.findById(notificacionDTO.getIdPlantilla())
                    .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada con ID: " + notificacionDTO.getIdPlantilla(), HttpStatus.NOT_FOUND));

            // Convertir datos de contexto a Map
            Map<String, Object> datosContexto = objectMapper.readValue(
                    notificacionDTO.getDatosContexto(),
                    objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class)
            );

            // Enviar email (con o sin adjuntos)
            if (notificacionDTO.getAdjuntos() != null && !notificacionDTO.getAdjuntos().isEmpty()) {
                emailService.enviarEmailConAdjuntos(
                        notificacionDTO.getDestinatario(),
                        plantilla.getAsunto(),
                        plantilla.getNombre(),
                        datosContexto,
                        notificacionDTO.getAdjuntos()
                );
            } else {
                emailService.enviarEmail(
                        notificacionDTO.getDestinatario(),
                        plantilla.getAsunto(),
                        plantilla.getNombre(),
                        datosContexto
                );
            }

            log.info("Notificación enviada a: {}", notificacionDTO.getDestinatario());
        } catch (Exception e) {
            log.error("Error al enviar notificación: {}", e.getMessage(), e);
            throw new NotificationException("Error al enviar notificación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void reintentarNotificacionesFallidas() {
        List<Notificacion> fallidas = notificacionRepository.findByEstado(EstadoNotificacion.ERROR);
        log.info("Reintentando {} notificaciones fallidas", fallidas.size());

        for (Notificacion notificacion : fallidas) {
            try {
                // Convertir a DTO usando MapStruct
                NotificacionDTO notificacionDTO = notificacionMapper.toDTO(notificacion);

                // Obtener adjuntos si existen
                List<Adjunto> adjuntosEntidad = adjuntoRepository.findByNotificacionIdNotificacion(notificacion.getIdNotificacion());
                if (!adjuntosEntidad.isEmpty()) {
                    List<AdjuntoDTO> adjuntos = adjuntosEntidad.stream()
                            .map(adjuntoMapper::toDTO)
                            .collect(Collectors.toList());
                    notificacionDTO.setAdjuntos(adjuntos);
                }

                enviarNotificacion(notificacionDTO);

                // Actualizar estado
                notificacion.setEstado(EstadoNotificacion.ENVIADO);
                notificacion.setFechaEnvio(LocalDateTime.now());
                notificacionRepository.save(notificacion);

            } catch (Exception e) {
                log.error("Error al reintentar notificación ID {}: {}",
                        notificacion.getIdNotificacion(), e.getMessage(), e);
            }
        }
    }

    @Override
    public List<NotificacionDTO> obtenerNotificacionesPorDestinatario(String destinatario, int pagina, int tamaño) {
        Page<Notificacion> notificacionesPage = notificacionRepository.findByDestinatario(
                destinatario,
                PageRequest.of(pagina, tamaño)
        );

        return notificacionesPage.getContent().stream()
                .map(notificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CircuitBreaker(name = "emailService", fallbackMethod = "enviarConfirmacionCitaFallback")
    public void enviarConfirmacionCita(CitaEvent citaEvent) {
        try {
            // Obtener información adicional mediante clientes Feign
            Map<String, Object> datosContexto = new HashMap<>();
            datosContexto.put("nombrePaciente", obtenerNombrePaciente(citaEvent.getIdPaciente()));
            datosContexto.put("nombreMedico", obtenerNombreMedico(citaEvent.getIdMedico()));
            datosContexto.put("especialidad", citaEvent.getEspecialidad());
            datosContexto.put("fecha", citaEvent.getFecha());
            datosContexto.put("hora", citaEvent.getHora());
            datosContexto.put("lugar", citaEvent.getConsultorio());

            // Buscar plantilla
            Plantilla plantilla = plantillaRepository.findByNombre("confirmacion-cita")
                    .orElseThrow(() -> new ResourceNotFoundException("Plantilla 'confirmacion-cita' no encontrada", HttpStatus.NOT_FOUND));

            // Enviar email
            emailService.enviarEmail(
                    citaEvent.getEmailPaciente(),
                    "Confirmación de Cita - HealthyMe",
                    plantilla.getNombre(),
                    datosContexto
            );

            log.info("Confirmación de cita enviada para ID: {}", citaEvent.getIdCita());

        } catch (Exception e) {
            log.error("Error al enviar confirmación de cita: {}", e.getMessage(), e);
            throw new NotificationException("Error al enviar confirmación de cita: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void enviarActualizacionCita(CitaEvent citaEvent) {
        try {
            Map<String, Object> datosContexto = new HashMap<>();
            datosContexto.put("nombrePaciente", obtenerNombrePaciente(citaEvent.getIdPaciente()));
            datosContexto.put("nombreMedico", obtenerNombreMedico(citaEvent.getIdMedico()));
            datosContexto.put("especialidad", citaEvent.getEspecialidad());
            datosContexto.put("fecha", citaEvent.getFecha());
            datosContexto.put("hora", citaEvent.getHora());
            datosContexto.put("lugar", citaEvent.getConsultorio());
            datosContexto.put("motivo", citaEvent.getMotivoCambio());

            Plantilla plantilla = plantillaRepository.findByNombre("actualizacion-cita")
                    .orElseThrow(() -> new ResourceNotFoundException("Plantilla 'actualizacion-cita' no encontrada", HttpStatus.NOT_FOUND));

            emailService.enviarEmail(
                    citaEvent.getEmailPaciente(),
                    "Actualización de su Cita - HealthyMe",
                    plantilla.getNombre(),
                    datosContexto
            );

            log.info("Actualización de cita enviada para ID: {}", citaEvent.getIdCita());
        } catch (Exception e) {
            log.error("Error al enviar actualización de cita: {}", e.getMessage(), e);
            throw new NotificationException("Error al enviar actualización de cita: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void enviarCancelacionCita(CitaEvent citaEvent) {
        try {
            Map<String, Object> datosContexto = new HashMap<>();
            datosContexto.put("nombrePaciente", obtenerNombrePaciente(citaEvent.getIdPaciente()));
            datosContexto.put("nombreMedico", obtenerNombreMedico(citaEvent.getIdMedico()));
            datosContexto.put("especialidad", citaEvent.getEspecialidad());
            datosContexto.put("fecha", citaEvent.getFecha());
            datosContexto.put("hora", citaEvent.getHora());
            datosContexto.put("motivo", citaEvent.getMotivoCambio());

            Plantilla plantilla = plantillaRepository.findByNombre("cancelacion-cita")
                    .orElseThrow(() -> new ResourceNotFoundException("Plantilla 'cancelacion-cita' no encontrada", HttpStatus.NOT_FOUND));

            emailService.enviarEmail(
                    citaEvent.getEmailPaciente(),
                    "Cancelación de Cita - HealthyMe",
                    plantilla.getNombre(),
                    datosContexto
            );

            log.info("Cancelación de cita enviada para ID: {}", citaEvent.getIdCita());
        } catch (Exception e) {
            log.error("Error al enviar cancelación de cita: {}", e.getMessage(), e);
            throw new NotificationException("Error al enviar cancelación de cita: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void enviarRecordatorioCita(CitaEvent citaEvent) {
        try {
            Map<String, Object> datosContexto = new HashMap<>();
            datosContexto.put("nombrePaciente", obtenerNombrePaciente(citaEvent.getIdPaciente()));
            datosContexto.put("nombreMedico", obtenerNombreMedico(citaEvent.getIdMedico()));
            datosContexto.put("especialidad", citaEvent.getEspecialidad());
            datosContexto.put("fecha", citaEvent.getFecha());
            datosContexto.put("hora", citaEvent.getHora());
            datosContexto.put("lugar", citaEvent.getConsultorio());

            Plantilla plantilla = plantillaRepository.findByNombre("recordatorio-cita")
                    .orElseThrow(() -> new ResourceNotFoundException("Plantilla 'recordatorio-cita' no encontrada", HttpStatus.NOT_FOUND));

            emailService.enviarEmail(
                    citaEvent.getEmailPaciente(),
                    "Recordatorio de Cita - HealthyMe",
                    plantilla.getNombre(),
                    datosContexto
            );

            log.info("Recordatorio de cita enviado para ID: {}", citaEvent.getIdCita());
        } catch (Exception e) {
            log.error("Error al enviar recordatorio de cita: {}", e.getMessage(), e);
            throw new NotificationException("Error al enviar recordatorio de cita: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void enviarReceta(RecetaEvent recetaEvent) {
        try {
            // Obtener detalles completos de la receta mediante cliente Feign
            Map<String, Object> datosReceta = obtenerDatosReceta(recetaEvent.getIdReceta());

            // Generar PDF
            AdjuntoDTO recetaPdf = pdfGenerationService.generarRecetaPdf(datosReceta);
            List<AdjuntoDTO> adjuntos = new ArrayList<>();
            adjuntos.add(recetaPdf);

            // Buscar plantilla
            Plantilla plantilla = plantillaRepository.findByNombre("receta-medica")
                    .orElseThrow(() -> new ResourceNotFoundException("Plantilla 'receta-medica' no encontrada", HttpStatus.NOT_FOUND));

            // Enviar email con adjunto
            emailService.enviarEmailConAdjuntos(
                    recetaEvent.getEmailPaciente(),
                    "Su Receta Médica - HealthyMe",
                    plantilla.getNombre(),
                    datosReceta,
                    adjuntos
            );

            log.info("Receta enviada para ID: {}", recetaEvent.getIdReceta());

        } catch (Exception e) {
            log.error("Error al enviar receta: {}", e.getMessage(), e);
            throw new NotificationException("Error al enviar receta: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void enviarResultadoExamen(ExamenEvent examenEvent) {
        try {
            // Obtener información del examen
            Map<String, Object> datosExamen = new HashMap<>(); // Aquí deberías llamar a un servicio para obtener los datos
            datosExamen.put("nombrePaciente", obtenerNombrePaciente(examenEvent.getIdPaciente()));
            datosExamen.put("tipoExamen", examenEvent.getTipoExamen());
            datosExamen.put("fecha", examenEvent.getFecha());
            datosExamen.put("resultados", examenEvent.getResultados());

            // Generar PDF de resultados
            AdjuntoDTO resultadoPdf = pdfGenerationService.generarResultadoLaboratorioPdf(datosExamen);
            List<AdjuntoDTO> adjuntos = new ArrayList<>();
            adjuntos.add(resultadoPdf);

            // Buscar plantilla
            Plantilla plantilla = plantillaRepository.findByNombre("resultado-laboratorio")
                    .orElseThrow(() -> new ResourceNotFoundException("Plantilla 'resultado-laboratorio' no encontrada", HttpStatus.NOT_FOUND));

            // Enviar email
            emailService.enviarEmailConAdjuntos(
                    examenEvent.getEmailPaciente(),
                    "Resultados de su Examen - HealthyMe",
                    plantilla.getNombre(),
                    datosExamen,
                    adjuntos
            );

            log.info("Resultados de examen enviados para ID: {}", examenEvent.getIdExamen());
        } catch (Exception e) {
            log.error("Error al enviar resultados de examen: {}", e.getMessage(), e);
            throw new NotificationException("Error al enviar resultados de examen: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<PlantillaDTO> obtenerTodasLasPlantillas() {
        return plantillaRepository.findAll().stream()
                .map(plantillaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlantillaDTO obtenerPlantilla(Integer idPlantilla) {
        return plantillaRepository.findById(idPlantilla)
                .map(plantillaMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada con ID: " + idPlantilla, HttpStatus.NOT_FOUND));
    }

    @Override
    public PlantillaDTO crearPlantilla(PlantillaDTO plantillaDTO) {
        Plantilla plantilla = plantillaMapper.toEntity(plantillaDTO);
        Plantilla guardada = plantillaRepository.save(plantilla);
        return plantillaMapper.toDTO(guardada);
    }

    @Override
    public PlantillaDTO actualizarPlantilla(Integer idPlantilla, PlantillaDTO plantillaDTO) {
        if (!plantillaRepository.existsById(idPlantilla)) {
            throw new ResourceNotFoundException("Plantilla no encontrada con ID: " + idPlantilla, HttpStatus.NOT_FOUND);
        }

        Plantilla plantilla = plantillaMapper.toEntity(plantillaDTO);
        plantilla.setIdPlantilla(idPlantilla);
        Plantilla actualizada = plantillaRepository.save(plantilla);
        return plantillaMapper.toDTO(actualizada);
    }

    @Override
    public void eliminarPlantilla(Integer idPlantilla) {
        if (!plantillaRepository.existsById(idPlantilla)) {
            throw new ResourceNotFoundException("Plantilla no encontrada con ID: " + idPlantilla, HttpStatus.NOT_FOUND);
        }
        plantillaRepository.deleteById(idPlantilla);
    }

    public void enviarNotificacionFallback(NotificacionDTO notificacionDTO, Exception ex) {
        log.error("Circuit breaker activado para enviarNotificacion: {}", ex.getMessage());

        try {
            // Guardar la notificación con estado ERROR para reintento posterior
            // Buscar la plantilla
            Plantilla plantilla = plantillaRepository.findById(notificacionDTO.getIdPlantilla())
                    .orElse(null);

            Notificacion notificacion = Notificacion.builder()
                    .destinatario(notificacionDTO.getDestinatario())
                    .fechaEnvio(LocalDateTime.now())
                    .estado(EstadoNotificacion.ERROR)
                    .plantilla(plantilla)
                    .datosContexto(notificacionDTO.getDatosContexto())
                    .entidadOrigen(notificacionDTO.getEntidadOrigen())
                    .idOrigen(notificacionDTO.getIdOrigen())
                    .build();

            notificacionRepository.save(notificacion);

            log.info("Notificación fallida guardada para reintento posterior: {}", notificacion.getIdNotificacion());
        } catch (Exception e) {
            log.error("Error al guardar notificación fallida: {}", e.getMessage());
        }
    }

    public String obtenerNombreMedicoFallback(Integer idMedico, Exception ex) {
        log.warn("Circuit breaker activado para obtenerNombreMedico: {}", ex.getMessage());
        return "Dr./Dra.";  // Valor por defecto
    }

    public String obtenerNombrePacienteFallback(Integer idPaciente, Exception ex) {
        log.warn("Circuit breaker activado para obtenerNombrePaciente: {}", ex.getMessage());
        return "Estimado Paciente";  // Valor por defecto
    }

    public Map<String, Object> obtenerDatosRecetaFallback(Integer idReceta, Exception ex) {
        log.warn("Circuit breaker activado para obtenerDatosReceta: {}", ex.getMessage());
        Map<String, Object> datosDefault = new HashMap<>();
        datosDefault.put("mensaje", "Los datos completos de la receta no están disponibles en este momento");
        datosDefault.put("idReceta", idReceta);
        datosDefault.put("fecha", LocalDateTime.now().toString());
        return datosDefault;
    }

    public void enviarConfirmacionCitaFallback(CitaEvent citaEvent, Exception ex) {
        log.error("Circuit breaker activado para enviarConfirmacionCita: {}", ex.getMessage());
        // Guardar el evento para procesamiento posterior
        eventPublisherService.publicarEventoFallido(
                citaEvent,
                "CITA",
                citaEvent.getTipoEvento().name(),
                citaEvent.getIdCita(),
                citaEvent.getEmailPaciente(),
                ex.getMessage()
        );
    }

    // Métodos auxiliares para comunicación con otros servicios mediante Feign
    @CircuitBreaker(name = "pacienteService", fallbackMethod = "obtenerNombrePacienteFallback")
    private String obtenerNombrePaciente(Long idPaciente) {
        try {
            PacienteDTO pacienteDTO = pacienteClient.findPacienteById(idPaciente).getBody();
            return "hola";
        } catch (Exception e) {
            log.warn("No se pudo obtener nombre del paciente ID {}: {}", idPaciente, e.getMessage());
            return "Estimado Paciente";
        }
    }

    @CircuitBreaker(name = "medicoService", fallbackMethod = "obtenerNombreMedicoFallback")
    private String obtenerNombreMedico(Integer idMedico) {
        try {
            return medicoClient.obtenerMedico(idMedico).getNombre();
        } catch (Exception e) {
            log.warn("No se pudo obtener nombre del médico ID {}: {}", idMedico, e.getMessage());
            return "Dr./Dra.";
        }
    }

    @CircuitBreaker(name = "recetaService", fallbackMethod = "obtenerDatosRecetaFallback")
    private Map<String, Object> obtenerDatosReceta(Integer idReceta) {
        try {
            return recetaClient.obtenerReceta(idReceta);
        } catch (Exception e) {
            log.warn("No se pudo obtener datos de la receta ID {}: {}", idReceta, e.getMessage());
            return new HashMap<>();
        }
    }
}