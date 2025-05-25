package studio.devbyjose.healthyme_payment.service.impl;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studio.devbyjose.healthyme_commons.enums.payment.EstadoPago;
import studio.devbyjose.healthyme_payment.dto.PaymentIntentDTO;
import studio.devbyjose.healthyme_payment.entity.Pago;
import studio.devbyjose.healthyme_payment.exception.PaymentProcessingException;
import studio.devbyjose.healthyme_payment.repository.PagoRepository;
import studio.devbyjose.healthyme_payment.service.interfaces.NotificationService;
import studio.devbyjose.healthyme_payment.service.interfaces.StripeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final PagoRepository pagoRepository;
    private final NotificationService notificationService;

    @Override
    public PaymentIntentDTO createPaymentIntent(BigDecimal monto, String descripcion, Map<String, String> metadata) {
        log.info("Creando PaymentIntent en Stripe para monto: {}", monto);

        try {
            // Convertir el monto a centavos para Stripe
            long montoEnCentavos = monto.multiply(new BigDecimal("100")).longValue();

            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(montoEnCentavos)
                    .setCurrency("pen") // Moneda peruana (PEN)
                    .setDescription(descripcion)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    );

            // Agregar metadatos si existen
            if (metadata != null && !metadata.isEmpty()) {
                for (Map.Entry<String, String> entry : metadata.entrySet()) {
                    paramsBuilder.putMetadata(entry.getKey(), entry.getValue());
                }
            }

            PaymentIntentCreateParams params = paramsBuilder.build();
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            PaymentIntentDTO paymentIntentDTO = new PaymentIntentDTO();
            paymentIntentDTO.setId(paymentIntent.getId());
            paymentIntentDTO.setClientSecret(paymentIntent.getClientSecret());
            paymentIntentDTO.setAmount(paymentIntent.getAmount());
            paymentIntentDTO.setCurrency(paymentIntent.getCurrency());
            paymentIntentDTO.setStatus(paymentIntent.getStatus());
            
            log.info("PaymentIntent creado exitosamente con ID: {}", paymentIntent.getId());
            return paymentIntentDTO;
        } catch (StripeException e) {
            log.error("Error al crear PaymentIntent en Stripe: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Error al procesar el pago con Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean confirmPayment(String paymentIntentId) {
        log.info("Confirmando pago con ID: {}", paymentIntentId);

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            // Solo se puede confirmar si aún no está confirmado o completado
            if ("requires_confirmation".equals(paymentIntent.getStatus())) {
                paymentIntent = paymentIntent.confirm();
            }
            
            boolean success = "succeeded".equals(paymentIntent.getStatus());
            log.info("Pago confirmado: {}. Estado: {}", success, paymentIntent.getStatus());
            
            return success;
        } catch (StripeException e) {
            log.error("Error al confirmar pago en Stripe: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Error al confirmar el pago con Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean refundPayment(String paymentIntentId, BigDecimal monto) {
        log.info("Procesando reembolso para pago con ID: {}. Monto: {}", paymentIntentId, monto);

        try {
            RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId);

            // Si se especifica un monto, convertirlo a centavos
            if (monto != null) {
                long montoEnCentavos = monto.multiply(new BigDecimal("100")).longValue();
                paramsBuilder.setAmount(montoEnCentavos);
            }

            Refund refund = Refund.create(paramsBuilder.build());
            
            boolean success = "succeeded".equals(refund.getStatus());
            log.info("Reembolso procesado: {}. Estado: {}", success, refund.getStatus());
            
            return success;
        } catch (StripeException e) {
            log.error("Error al procesar reembolso en Stripe: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Error al procesar el reembolso con Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public String handleWebhookEvent(String payload, String sigHeader) {
        log.info("Procesando evento webhook de Stripe");

        try {
            // Verificar la firma del evento para asegurar que proviene de Stripe
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            // Obtener el tipo de evento
            String eventType = event.getType();
            log.info("Evento de tipo: {}", eventType);

            // Obtener el objeto de datos del evento
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

            if (dataObjectDeserializer.getObject().isEmpty()) {
                throw new PaymentProcessingException("Error al deserializar el objeto del evento");
            }

            // Procesar diferentes tipos de eventos
            switch (eventType) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(dataObjectDeserializer.getObject().get());
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(dataObjectDeserializer.getObject().get());
                    break;
                case "payment_intent.canceled":
                    handlePaymentIntentCanceled(dataObjectDeserializer.getObject().get());
                    break;
                case "charge.refunded":
                    handleChargeRefunded(dataObjectDeserializer.getObject().get());
                    break;
                case "charge.dispute.created":
                    handleChargeDisputeCreated(dataObjectDeserializer.getObject().get());
                    break;
                default:
                    log.info("Evento no manejado explícitamente: {}", eventType);
            }

            return "Webhook procesado correctamente";
        } catch (SignatureVerificationException e) {
            log.error("Error de verificación de firma: {}", e.getMessage());
            throw new PaymentProcessingException("La firma del webhook no es válida", e);
        } catch (Exception e) {
            log.error("Error al procesar webhook: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Error al procesar el webhook de Stripe: " + e.getMessage(), e);
        }
    }

    // Métodos privados para manejar diferentes tipos de eventos
    private void handlePaymentIntentSucceeded(Object dataObject) {
        PaymentIntent paymentIntent = (PaymentIntent) dataObject;
        log.info("Pago exitoso con ID: {}", paymentIntent.getId());

        // Buscar el pago correspondiente a este PaymentIntent
        actualizarEstadoPago(paymentIntent.getId(), EstadoPago.COMPLETADO);
    }

    private void handlePaymentIntentFailed(Object dataObject) {
        PaymentIntent paymentIntent = (PaymentIntent) dataObject;
        log.info("Pago fallido con ID: {}", paymentIntent.getId());

        // Actualizar estado del pago
        actualizarEstadoPago(paymentIntent.getId(), EstadoPago.FALLIDO);

        // Enviar notificación de pago fallido
        notificationService.notificarPagoFallido(paymentIntent.getId(),
                paymentIntent.getLastPaymentError() != null ? paymentIntent.getLastPaymentError().getMessage() : "Error desconocido");
    }

    private void handlePaymentIntentCanceled(Object dataObject) {
        PaymentIntent paymentIntent = (PaymentIntent) dataObject;
        log.info("Pago cancelado con ID: {}", paymentIntent.getId());

        // Actualizar estado del pago
        actualizarEstadoPago(paymentIntent.getId(), EstadoPago.CANCELADO);
    }

    private void handleChargeRefunded(Object dataObject) {
        com.stripe.model.Charge charge = (com.stripe.model.Charge) dataObject;
        log.info("Reembolso recibido para charge ID: {}", charge.getId());

        // Buscar el pago relacionado con este cargo
        String paymentIntentId = charge.getPaymentIntent();
        if (paymentIntentId != null) {
            // Actualizar el estado del pago a REEMBOLSADO
            actualizarEstadoPago(paymentIntentId, EstadoPago.REEMBOLSADO);

            // Enviar notificación de reembolso
            notificationService.notificarPagoReembolsado(paymentIntentId);
        } else {
            log.warn("No se encontró PaymentIntent ID para el cargo reembolsado: {}", charge.getId());
        }
    }

    private void handleChargeDisputeCreated(Object dataObject) {
        // Implementar lógica para manejar disputas
        log.info("Disputa creada: {}", dataObject);
        // Aquí actualizaríamos el estado del pago a EN_DISPUTA
    }

    private void actualizarEstadoPago(String paymentIntentId, EstadoPago estado) {
        Optional<Pago> optionalPago = pagoRepository.findByPaymentIntentId(paymentIntentId);
        if (optionalPago.isPresent()) {
            Pago pago = optionalPago.get();
            pago.setEstado(estado);
            pago.setUltimaModificacion(LocalDateTime.now());
            pagoRepository.save(pago);
            log.info("Estado de pago actualizado a {} para paymentIntent: {}", estado, paymentIntentId);
        } else {
            log.warn("No se encontró pago asociado al paymentIntent: {}", paymentIntentId);
        }
    }

}