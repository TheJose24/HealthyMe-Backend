package studio.devbyjose.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/pacientes")
    public ResponseEntity<Map<String, Object>> pacientesFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("code", 503);
        response.put("message", "El servicio de pacientes no está disponible en este momento. Por favor, inténtelo más tarde.");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("code", 503);
        response.put("message", "El servicio de notificaciones no está disponible en este momento.");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @GetMapping("/storage")
    public ResponseEntity<Map<String, Object>> storageFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("code", 503);
        response.put("message", "El servicio de almacenamiento no está disponible en este momento.");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    // Metodo genérico para otros servicios
    @GetMapping("/default")
    public ResponseEntity<Map<String, Object>> defaultFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("code", 503);
        response.put("message", "El servicio solicitado no está disponible en este momento.");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}