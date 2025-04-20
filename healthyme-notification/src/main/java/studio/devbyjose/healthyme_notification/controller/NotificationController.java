package studio.devbyjose.healthyme_notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_notification.dto.NotificacionDTO;
import studio.devbyjose.healthyme_notification.dto.PlantillaDTO;
import studio.devbyjose.healthyme_notification.service.interfaces.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Void> enviarNotificacion(@Valid @RequestBody NotificacionDTO notificacionDTO) {
        notificationService.enviarNotificacion(notificacionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/destinatario/{email}")
    public ResponseEntity<List<NotificacionDTO>> obtenerNotificacionesPorDestinatario(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        List<NotificacionDTO> notificaciones =
                notificationService.obtenerNotificacionesPorDestinatario(email, pagina, tamanio);

        return ResponseEntity.ok(notificaciones);
    }

    @PostMapping("/reintentar")
    public ResponseEntity<Void> reintentarNotificacionesFallidas() {
        notificationService.reintentarNotificacionesFallidas();
        return ResponseEntity.accepted().build();
    }

    // Endpoints de plantillas

    @GetMapping("/plantillas")
    public ResponseEntity<List<PlantillaDTO>> obtenerTodasLasPlantillas() {
        List<PlantillaDTO> plantillas = notificationService.obtenerTodasLasPlantillas();
        return ResponseEntity.ok(plantillas);
    }

    @GetMapping("/plantillas/{id}")
    public ResponseEntity<PlantillaDTO> obtenerPlantillaPorId(@PathVariable Integer id) {
        PlantillaDTO plantilla = notificationService.obtenerPlantilla(id);
        return ResponseEntity.ok(plantilla);
    }

    @PostMapping("/plantillas")
    public ResponseEntity<PlantillaDTO> crearPlantilla(@Valid @RequestBody PlantillaDTO plantillaDTO) {
        PlantillaDTO creada = notificationService.crearPlantilla(plantillaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/plantillas/{id}")
    public ResponseEntity<PlantillaDTO> actualizarPlantilla(
            @PathVariable Integer id,
            @Valid @RequestBody PlantillaDTO plantillaDTO) {

        PlantillaDTO actualizada = notificationService.actualizarPlantilla(id, plantillaDTO);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/plantillas/{id}")
    public ResponseEntity<Void> eliminarPlantilla(@PathVariable Integer id) {
        notificationService.eliminarPlantilla(id);
        return ResponseEntity.noContent().build();
    }
}