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

}