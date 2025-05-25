package studio.devbyjose.healthyme_payment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_commons.client.dto.NotificacionDTO;
import studio.devbyjose.healthyme_commons.client.dto.PacienteDTO;
import studio.devbyjose.healthyme_commons.client.dto.UsuarioDTO;
import studio.devbyjose.healthyme_commons.client.feign.NotificationClient;
import studio.devbyjose.healthyme_commons.client.feign.PacienteClient;
import studio.devbyjose.healthyme_commons.client.feign.UsuarioClient;
import studio.devbyjose.healthyme_commons.enums.EntidadOrigen;
import studio.devbyjose.healthyme_payment.entity.Factura;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.repository.PagoRepository;
import studio.devbyjose.healthyme_payment.service.interfaces.NotificationService;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationClient notificationClient;
    private final PagoRepository pagoRepository;
    private final PacienteClient pacienteClient;
    private final UsuarioClient usuarioClient;

    @Override
    public void notificarPagoExitoso(String paymentIntentId) {
        log.info("Enviando notificación de pago completado para ID: {}", paymentIntentId);

        Optional<Pago> pagoOpt = pagoRepository.findByPaymentIntentId(paymentIntentId);
        if (pagoOpt.isPresent()) {
            Pago pago = pagoOpt.get();
            log.info("Enviando notificación de pago exitoso para ID: {}", pago.getId());

            try {
                // Preparar datos de contexto como JSON String directamente
                String datosContextoJson = String.format(
                        "{\n" +
                                "  \"monto\": \"%s\",\n" +
                                "  \"fecha\": \"%s\",\n" +
                                "  \"idPago\": \"%d\",\n" +
                                "  \"concepto\": \"%s\"\n" +
                                "}",
                        pago.getMonto(), pago.getFechaPago(), pago.getId(), obtenerConceptoPago(pago));

                // Construir la notificación
                NotificacionDTO notificacion = NotificacionDTO.builder()
                        .destinatario("paciente_" + pago.getIdPaciente() + "@healthyme.com") // Aquí deberías obtener el email real
                        .idPlantilla(1) // ID de la plantilla para notificaciones de pago exitoso
                        .datosContexto(datosContextoJson)
                        .entidadOrigen(EntidadOrigen.PAGO)
                        .idOrigen(pago.getId())
                        .build();

                ResponseEntity<Void> response = notificationClient.enviarNotificacion(notificacion);
                log.info("Notificación enviada con estado: {}", response.getStatusCode());
            } catch (Exception e) {
                log.error("Error al enviar notificación: {}", e.getMessage(), e);
            }
        } else {
            log.warn("No se pudo enviar notificación. Pago no encontrado para paymentIntentId: {}", paymentIntentId);
        }
    }

    @Override
    public boolean enviarFacturaPorEmail(Factura factura, String emailDestino) {
        log.info("Preparando para enviar factura {} por email a: {}", factura.getNumeroFactura(), emailDestino);

        try {
            // Obtener información del paciente
            ResponseEntity<PacienteDTO> pacienteResponse = pacienteClient.findPacienteById(factura.getPago().getIdPaciente().longValue());
            ResponseEntity<UsuarioDTO> usuarioResponse = usuarioClient.obtenerUsuario(pacienteResponse.getBody().getIdUsuario().intValue());

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Preparar los datos completos de la factura
            Map<String, Object> datosFactura = new HashMap<>();
            datosFactura.put("numero_factura", factura.getNumeroFactura());
            datosFactura.put("fecha_emision", factura.getFechaEmision().format(dateFormatter));
            datosFactura.put("fecha_pago", factura.getPago().getFechaPago().format(dateTimeFormatter));
            datosFactura.put("subtotal", factura.getSubtotal());
            datosFactura.put("impuestos", factura.getImpuestos());
            datosFactura.put("total", factura.getTotal());
            datosFactura.put("id_pago", factura.getPago().getId());
            datosFactura.put("id_factura", factura.getId());
            datosFactura.put("email", emailDestino);
            datosFactura.put("nombre_paciente", usuarioResponse.getBody().getPersona().getNombre() + " " + usuarioResponse.getBody().getPersona().getApellido());
            datosFactura.put("dni_paciente", usuarioResponse.getBody().getPersona().getDni());
            datosFactura.put("email_paciente", emailDestino);
            datosFactura.put("concepto", obtenerConceptoPago(factura.getPago()));
            datosFactura.put("metodo_pago", "Tarjeta de Crédito");

            // Convertir a JSON para datosContexto
            String datosContextoJson;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                datosContextoJson = objectMapper.writeValueAsString(datosFactura);
            } catch (Exception e) {
                log.error("Error al serializar datos de factura", e);
                return false;
            }

            // Crear NotificacionDTO para envío de factura
            NotificacionDTO notificacionFactura = NotificacionDTO.builder()
                    .destinatario(emailDestino)
                    .idPlantilla(3) // ID de la plantilla para facturas por email
                    .datosContexto(datosContextoJson)
                    .entidadOrigen(EntidadOrigen.SISTEMA)
                    .idOrigen(factura.getId())
                    .build();

            // Enviar usando el endpoint principal del servicio de notificaciones
            ResponseEntity<Void> response = notificationClient.enviarNotificacion(notificacionFactura);

            boolean enviado = response.getStatusCode().is2xxSuccessful();
            if (enviado) {
                log.info("Factura {} enviada correctamente por email", factura.getNumeroFactura());
            } else {
                log.warn("El servicio de notificaciones reportó error al enviar la factura {}", factura.getNumeroFactura());
            }

            return enviado;
        } catch (Exception e) {
            log.error("Error al enviar factura por email: {}", e.getMessage(), e);
            return false;
        }
    }

    private String obtenerConceptoPago(Pago pago) {
        switch (pago.getEntidadReferencia()) {
            case CITA:
                return "Consulta Médica";
            case EXAMEN:
                return "Examen de Laboratorio";
            case RECETA:
                return "Medicamentos";
            default:
                return "Servicio Médico";
        }
    }

    @Override
    public void notificarPagoFallido(String paymentIntentId, String mensaje) {
        Optional<Pago> pagoOpt = pagoRepository.findByPaymentIntentId(paymentIntentId);

        if (pagoOpt.isPresent()) {
            Pago pago = pagoOpt.get();
            log.info("Enviando notificación de pago fallido para ID: {}", pago.getId());

            // Construir la notificación
            NotificacionDTO notificacion = NotificacionDTO.builder()
                    .datosContexto("Su pago por " + pago.getMonto() + " no pudo ser procesado. Motivo: " + mensaje)
                    .entidadOrigen(pago.getEntidadReferencia())
                    .idOrigen(pago.getEntidadReferenciaId())
                    .build();

            // Enviar notificación al servicio de notificaciones
            try {
                ResponseEntity<Void> response = notificationClient.enviarNotificacion(notificacion);
                log.info("Notificación de pago fallido enviada para el pago ID: {} con respuesta: {}",
                        pago.getId(), response.getStatusCode());
            } catch (Exception e) {
                log.error("Error al enviar notificación de pago fallido: {}", e.getMessage(), e);
            }
        } else {
            log.warn("No se pudo enviar notificación. Pago no encontrado para paymentIntentId: {}", paymentIntentId);
        }
    }

    @Override
    public void notificarPagoReembolsado(String paymentIntentId) {
        Optional<Pago> pagoOpt = pagoRepository.findByPaymentIntentId(paymentIntentId);

        if (pagoOpt.isPresent()) {
            Pago pago = pagoOpt.get();
            log.info("Enviando notificación de reembolso para ID: {}", pago.getId());

            // Construir la notificación
            NotificacionDTO notificacion = NotificacionDTO.builder()
                    .datosContexto("El reembolso por " + pago.getMonto() + " ha sido procesado exitosamente.")
                    .entidadOrigen(pago.getEntidadReferencia())
                    .idOrigen(pago.getEntidadReferenciaId())
                    .build();

            // Enviar notificación al servicio de notificaciones
            try {
                ResponseEntity<Void> response = notificationClient.enviarNotificacion(notificacion);
                log.info("Notificación de reembolso enviada para el pago ID: {} con respuesta: {}",
                        pago.getId(), response.getStatusCode());
            } catch (Exception e) {
                log.error("Error al enviar notificación de reembolso: {}", e.getMessage(), e);
            }
        } else {
            log.warn("No se pudo enviar notificación. Pago no encontrado para paymentIntentId: {}", paymentIntentId);
        }
    }


}