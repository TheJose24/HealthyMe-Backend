package studio.devbyjose.healthyme_payment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import studio.devbyjose.healthyme_commons.client.dto.NotificacionDTO;
import studio.devbyjose.healthyme_commons.client.feign.NotificationClient;
import studio.devbyjose.healthyme_commons.client.feign.PacienteClient;
import studio.devbyjose.healthyme_commons.client.feign.SecurityClient;
import studio.devbyjose.healthyme_payment.entity.Factura;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.repository.PagoRepository;
import studio.devbyjose.healthyme_payment.service.interfaces.NotificationService;

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
    private final SecurityClient securityClient;

    @Override
    public void notificarPagoExitoso(String paymentIntentId) {
        log.info("Enviando notificación de pago completado para ID: {}", paymentIntentId);

        Optional<Pago> pagoOpt = pagoRepository.findByPaymentIntentId(paymentIntentId);
        if (pagoOpt.isPresent()) {
            Pago pago = pagoOpt.get();
            log.info("Enviando notificación de pago exitoso para ID: {}", pago.getId());

            // Verificar si el paciente existe y obtener id usuario para el email
            Long idUsuarioPaciente = Objects.requireNonNull(pacienteClient.findPacienteById(pago.getIdPaciente())
                    .getBody()).getIdUsuario();

            // Obtener el email del paciente
            String emailPaciente = Objects.requireNonNull(securityClient.getUsuarioById(Math.toIntExact(idUsuarioPaciente))
                    .getBody()).getPersona().getEmail();


            // Preparar datos de contexto
            Map<String, Object> datosContexto = new HashMap<>();
            datosContexto.put("monto", pago.getMonto().toString());
            datosContexto.put("fecha", pago.getFechaPago().toString());
            datosContexto.put("idPago", pago.getId().toString());

            // Convertir a formato JSON para datosContexto
            String datosContextoJson;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                datosContextoJson = objectMapper.writeValueAsString(datosContexto);
            } catch (Exception e) {
                datosContextoJson = "{\"monto\":\"" + pago.getMonto() + "\",\"fecha\":\"" +
                        pago.getFechaPago() + "\"}";
            }

            // Construir la notificación con los campos requeridos por el servicio
            NotificacionDTO notificacion = NotificacionDTO.builder()
                    .datosContexto("Su pago por " + pago.getMonto() + " ha sido procesado exitosamente.")
                    .destinatario(emailPaciente)
                    .entidadOrigen(pago.getEntidadReferencia())
                    .idOrigen(pago.getEntidadReferenciaId())
                    // Campos específicos para el servicio de notificaciones
                    .idPlantilla(1) // ID de la plantilla para notificaciones de pago exitoso
                    .datosContexto(datosContextoJson)
                    .idOrigen(pago.getId())
                    .build();

            // Enviar notificación al servicio de notificaciones
            try {
                ResponseEntity<Void> response = notificationClient.enviarNotificacion(notificacion);
                log.info("Notificación de pago exitoso enviada para el pago ID: {} con respuesta: {}",
                        pago.getId(), response.getStatusCode());
            } catch (Exception e) {
                log.error("Error al enviar notificación de pago exitoso: {}", e.getMessage(), e);
            }
        } else {
            log.warn("No se pudo enviar notificación. Pago no encontrado para paymentIntentId: {}", paymentIntentId);
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

    @Override
    public boolean enviarFacturaPorEmail(Factura factura, String emailDestino) {
        log.info("Preparando para enviar factura {} por email a: {}", factura.getNumeroFactura(), emailDestino);

        try {
            // Preparar los datos de la factura
            Map<String, Object> datosFactura = new HashMap<>();
            datosFactura.put("id_factura", factura.getId());
            datosFactura.put("numero_factura", factura.getNumeroFactura());
            datosFactura.put("fecha_emision", factura.getFechaEmision().toString());
            datosFactura.put("subtotal", factura.getSubtotal().toString());
            datosFactura.put("impuestos", factura.getImpuestos().toString());
            datosFactura.put("total", factura.getTotal().toString());
            datosFactura.put("id_pago", factura.getPago().getId());
            datosFactura.put("email", emailDestino);
            datosFactura.put("id_paciente", factura.getPago().getIdPaciente());

            // Enviar al servicio de notificaciones
            ResponseEntity<Boolean> response = notificationClient.enviarFacturaPorEmail(datosFactura);

            boolean enviado = response.getBody() != null && response.getBody();
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
}