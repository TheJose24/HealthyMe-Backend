package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import studio.devbyjose.healthyme_commons.client.dto.NotificacionDTO;
import studio.devbyjose.healthyme_commons.client.fallback.NotificationClientFallback;

import java.util.Map;

@FeignClient(name = "healthyme-notification",
        path = "/api/notificaciones",
        fallback = NotificationClientFallback.class)
public interface NotificationClient {

    @PostMapping
    ResponseEntity<Void> enviarNotificacion(@RequestBody NotificacionDTO notificacion);

    @PostMapping("/email-con-factura")
    ResponseEntity<Boolean> enviarFacturaPorEmail(@RequestBody Map<String, Object> datos);

    @PostMapping("/pago-completado")
    ResponseEntity<Boolean> notificarPagoCompletado(@RequestBody Map<String, Object> datosPago);
}