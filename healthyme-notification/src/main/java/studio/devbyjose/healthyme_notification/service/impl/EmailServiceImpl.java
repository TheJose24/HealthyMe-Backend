package studio.devbyjose.healthyme_notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import studio.devbyjose.healthyme_commons.client.dto.AdjuntoDTO;
import studio.devbyjose.healthyme_commons.client.dto.FileMetadataDTO;
import studio.devbyjose.healthyme_commons.client.feign.StorageClient;
import studio.devbyjose.healthyme_notification.entity.Adjunto;
import studio.devbyjose.healthyme_notification.entity.Notificacion;
import studio.devbyjose.healthyme_notification.entity.Plantilla;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_commons.enums.notification.EstadoNotificacion;
import studio.devbyjose.healthyme_notification.exception.NotificationException;
import studio.devbyjose.healthyme_notification.repository.NotificacionRepository;
import studio.devbyjose.healthyme_notification.repository.PlantillaRepository;
import studio.devbyjose.healthyme_notification.service.interfaces.EmailService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final NotificacionRepository notificacionRepository;
    private final PlantillaRepository plantillaRepository;
    private final StorageClient storageClient;

    @Value("${spring.mail.username}")
    private String remitente;

    @Override
    @CircuitBreaker(name = "emailService", fallbackMethod = "enviarEmailFallback")
    public void enviarEmail(String destinatario, String asunto, String nombrePlantilla, Map<String, Object> model) {
        try {
            enviarEmailInterno(destinatario, asunto, nombrePlantilla, model, null, EntidadOrigen.CITA, null);
        } catch (Exception e) {
            log.error("Error al enviar correo electrónico a {}: {}", destinatario, e.getMessage());
            throw new NotificationException("Error al enviar correo electrónico: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @CircuitBreaker(name = "emailService", fallbackMethod = "enviarEmailConAdjuntosFallback")
    public void enviarEmailConAdjuntos(String destinatario, String asunto, String nombrePlantilla,
                                       Map<String, Object> model, List<AdjuntoDTO> adjuntos) {
        try {
            enviarEmailInterno(destinatario, asunto, nombrePlantilla, model, adjuntos, EntidadOrigen.CITA, null);
        } catch (Exception e) {
            log.error("Error al enviar correo electrónico con adjuntos a {}: {}", destinatario, e.getMessage());
            throw new NotificationException("Error al enviar correo electrónico con adjuntos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Implementación completa para envío de correos con entidad origen e ID origen
    public void enviarEmailRelacionado(String destinatario, String asunto, String nombrePlantilla,
                                       Map<String, Object> model, List<AdjuntoDTO> adjuntos,
                                       EntidadOrigen entidadOrigen, Integer idOrigen) {
        try {
            enviarEmailInterno(destinatario, asunto, nombrePlantilla, model, adjuntos, entidadOrigen, idOrigen);
        } catch (Exception e) {
            log.error("Error al enviar correo relacionado a {}: {}", destinatario, e.getMessage());
            throw new NotificationException("Error al enviar correo relacionado: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void enviarEmailInterno(String destinatario, String asunto, String nombrePlantilla,
                                    Map<String, Object> model, List<AdjuntoDTO> adjuntos,
                                    EntidadOrigen entidadOrigen, Integer idOrigen) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                adjuntos != null && !adjuntos.isEmpty(),
                StandardCharsets.UTF_8.name());

        // Configurar remitente y destinatario
        helper.setFrom(remitente);
        helper.setTo(destinatario);
        helper.setSubject(asunto);

        // Procesar la plantilla
        Context context = new Context();
        if (model != null) {
            model.forEach(context::setVariable);
        }
        String html = templateEngine.process(nombrePlantilla, context);
        helper.setText(html, true);

        // Agregar adjuntos si existen
        if (adjuntos != null && !adjuntos.isEmpty()) {
            for (AdjuntoDTO adjunto : adjuntos) {
                if (adjunto.getStorageFilename() != null && !adjunto.getStorageFilename().isEmpty()) {
                    try {
                        ResponseEntity<byte[]> response = storageClient.getFile(adjunto.getStorageFilename());

                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                            // Obtener metadatos para el nombre y tipo de contenido
                            ResponseEntity<FileMetadataDTO> metadataResponse =
                                    storageClient.getFileMetadata(adjunto.getStorageFilename());

                            FileMetadataDTO metadata = metadataResponse.getBody();
                            if (metadata != null) {
                                String nombreArchivo = metadata.getOriginalFilename();
                                String tipoContenido = metadata.getContentType();

                                helper.addAttachment(
                                        nombreArchivo,
                                        new ByteArrayResource(response.getBody()),
                                        tipoContenido
                                );

                                log.info("Adjunto obtenido del servicio de almacenamiento: {}", nombreArchivo);
                            }
                        } else {
                            log.error("No se pudo obtener el archivo {} del servicio de almacenamiento",
                                    adjunto.getStorageFilename());
                        }
                    } catch (Exception e) {
                        log.error("Error al obtener archivo del servicio de almacenamiento: {}",
                                e.getMessage(), e);
                    }
                } else if (adjunto.getContenido() != null) {
                    helper.addAttachment(
                            adjunto.getNombre(),
                            new ByteArrayResource(adjunto.getContenido()),
                            adjunto.getTipoContenido()
                    );
                }
            }
        }

        // Enviar el correo
        emailSender.send(message);
        log.info("Correo electrónico enviado exitosamente a: {}", destinatario);

        // Buscar la plantilla por nombre
        Optional<Plantilla> plantillaOpt = plantillaRepository.findByNombre(nombrePlantilla);
        if (plantillaOpt.isEmpty()) {
            log.warn("No se encontró la plantilla '{}' al registrar la notificación", nombrePlantilla);
            return;
        }

        // Registrar la notificación
        Notificacion notificacion = Notificacion.builder()
                .destinatario(destinatario)
                .fechaEnvio(LocalDateTime.now())
                .estado(EstadoNotificacion.ENVIADO)
                .plantilla(plantillaOpt.get())
                .datosContexto(convertirModelAJson(model))
                .entidadOrigen(entidadOrigen != null ? entidadOrigen : EntidadOrigen.CITA)
                .idOrigen(idOrigen)
                .build();

        notificacionRepository.save(notificacion);
    }

    public void enviarEmailFallback(String to, String subject, String templateName,
                                    Map<String, Object> model, List<Adjunto> adjuntos,
                                    Exception ex) {
        log.warn("Circuit breaker activado. No se pudo enviar email a: {}", to);
        // Guardar en cola para reintento posterior
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(to);
        notificacion.setEstado(EstadoNotificacion.ERROR);
        notificacion.setDatosContexto(new ObjectMapper().valueToTree(model).asText());
        // Demás campos de notificación...
        notificacionRepository.save(notificacion);
    }

    // Metodo fallback para enviarEmail
    public void enviarEmailFallback(String destinatario, String asunto, String nombrePlantilla,
                                    Map<String, Object> model, Exception ex) {
        log.warn("Circuit breaker activado para enviarEmail. Destinatario: {}, Error: {}",
                destinatario, ex.getMessage());

        guardarNotificacionFallida(destinatario, nombrePlantilla, model,
                EntidadOrigen.CITA, null);
    }

    // Metodo fallback para enviarEmailConAdjuntos
    public void enviarEmailConAdjuntosFallback(String destinatario, String asunto, String nombrePlantilla,
                                               Map<String, Object> model, List<AdjuntoDTO> adjuntos, Exception ex) {
        log.warn("Circuit breaker activado para enviarEmailConAdjuntos. Destinatario: {}, Error: {}",
                destinatario, ex.getMessage());

        guardarNotificacionFallida(destinatario, nombrePlantilla, model,
                EntidadOrigen.CITA, null);
    }

    private void guardarNotificacionFallida(String destinatario, String nombrePlantilla,
                                            Map<String, Object> model, EntidadOrigen entidadOrigen,
                                            Integer idOrigen) {
        try {
            // Buscar la plantilla por nombre
            Optional<Plantilla> plantillaOpt = plantillaRepository.findByNombre(nombrePlantilla);
            Plantilla plantilla = plantillaOpt.orElse(null);

            // Guardar notificación con estado ERROR
            Notificacion notificacion = Notificacion.builder()
                    .destinatario(destinatario)
                    .fechaEnvio(LocalDateTime.now())
                    .estado(EstadoNotificacion.ERROR)
                    .plantilla(plantilla)  // Puede ser null si no se encuentra
                    .datosContexto(convertirModelAJson(model))
                    .entidadOrigen(entidadOrigen)
                    .idOrigen(idOrigen)
                    .build();

            notificacionRepository.save(notificacion);
            log.info("Notificación fallida guardada para reintento posterior: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al guardar notificación fallida: {}", e.getMessage(), e);
        }
    }

    private String convertirModelAJson(Map<String, Object> model) {
        // Implementación simple - en producción usar Jackson ObjectMapper
        if (model == null || model.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
        }

        sb.append("}");
        return sb.toString();
    }

}
