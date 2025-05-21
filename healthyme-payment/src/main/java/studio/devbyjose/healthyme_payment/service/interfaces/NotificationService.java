package studio.devbyjose.healthyme_payment.service.interfaces;

import studio.devbyjose.healthyme_payment.entity.Factura;

public interface NotificationService {
    /**
     * Envía una notificación cuando un pago se ha completado exitosamente
     *
     * @param paymentIntentId ID del pago en Stripe
     */
    void notificarPagoExitoso(String paymentIntentId);

    /**
     * Envía una notificación cuando un pago ha fallado
     *
     * @param paymentIntentId ID del pago en Stripe
     * @param mensaje Mensaje de error
     */
    void notificarPagoFallido(String paymentIntentId, String mensaje);

    /**
     * Envía una notificación cuando un pago ha sido reembolsado
     *
     * @param paymentIntentId ID del pago en Stripe
     */
    void notificarPagoReembolsado(String paymentIntentId);

    /**
     * Envía una factura por correo electrónico
     *
     * @param factura La factura a enviar
     * @param emailDestino Dirección de correo electrónico del destinatario
     * @return true si se envió correctamente, false en caso contrario
     */
    boolean enviarFacturaPorEmail(Factura factura, String emailDestino);
}