package studio.devbyjose.healthyme_commons.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import studio.devbyjose.healthyme_commons.client.dto.NotificacionDTO;
import studio.devbyjose.healthyme_commons.client.fallback.NotificationClientFallback;

import java.util.Map;

@FeignClient(name = "healthyme-notification",
        path = "/api/v1/notificaciones",
        fallback = NotificationClientFallback.class)
public interface NotificationClient {

    @PostMapping
    ResponseEntity<Void> enviarNotificacion(@RequestBody NotificacionDTO notificacion);

}