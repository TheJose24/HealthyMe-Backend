package studio.devbyjose.healthyme_payment.service.interfaces;

import studio.devbyjose.healthyme_payment.dto.PaymentIntentDTO;

import java.math.BigDecimal;
import java.util.Map;

public interface StripeService {
    
    /**
     * Crea una intención de pago en Stripe
     * @param monto monto a pagar
     * @param descripcion descripción del pago
     * @param metadata metadatos adicionales
     * @return mapa con el ID de la intención y el secreto del cliente
     */
    PaymentIntentDTO createPaymentIntent(BigDecimal monto, String descripcion, Map<String, String> metadata);
    
    /**
     * Confirma un pago en Stripe
     * @param paymentIntentId ID de la intención de pago
     * @return true si el pago fue confirmado exitosamente
     */
    boolean confirmPayment(String paymentIntentId);
    
    /**
     * Reembolsa un pago
     * @param paymentIntentId ID de la intención de pago
     * @param monto monto a reembolsar (null para reembolso total)
     * @return true si el reembolso fue exitoso
     */
    boolean refundPayment(String paymentIntentId, BigDecimal monto);
    
    /**
     * Procesa un evento de webhook de Stripe
     * @param payload contenido del webhook
     * @param sigHeader encabezado de firma
     * @return resultado del procesamiento
     */
    String handleWebhookEvent(String payload, String sigHeader);
}