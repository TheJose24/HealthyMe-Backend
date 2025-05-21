package studio.devbyjose.healthyme_commons.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import studio.devbyjose.healthyme_commons.client.dto.NotificacionDTO;
import studio.devbyjose.healthyme_commons.client.feign.NotificationClient;

import java.util.Map;

@Component
@Slf4j
public class NotificationClientFallback implements NotificationClient {

    @Override
    public ResponseEntity<Void> enviarNotificacion(NotificacionDTO notificacion) {
        log.error("Error al enviar notificación: {}", notificacion);
        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<Boolean> enviarFacturaPorEmail(Map<String, Object> datos) {
        log.error("Error al enviar factura por email: {}", datos);
        return ResponseEntity.ok(false);
    }

    @Override
    public ResponseEntity<Boolean> notificarPagoCompletado(Map<String, Object> datosPago) {
        log.error("Error al notificar pago completado: {}", datosPago);
        return ResponseEntity.ok(false);
    }
}