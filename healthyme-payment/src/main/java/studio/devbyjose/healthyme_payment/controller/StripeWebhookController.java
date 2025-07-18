package studio.devbyjose.healthyme_payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_payment.exception.PaymentProcessingException;
import studio.devbyjose.healthyme_payment.service.interfaces.StripeService;

@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Stripe Webhook", description = "Endpoint para recibir eventos de Stripe")
public class StripeWebhookController {

    private final StripeService stripeService;

    @PostMapping
    @Operation(summary = "Procesar webhook de Stripe",
            description = "Recibe eventos de Stripe y los procesa según el tipo de evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento procesado correctamente"),
            @ApiResponse(responseCode = "400", description = "Evento inválido o firma incorrecta"),
            @ApiResponse(responseCode = "500", description = "Error al procesar el evento")
    })
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = true) String sigHeader) {

        log.info("Webhook de Stripe recibido con firma: {}", sigHeader);

        try {
            String result = stripeService.handleWebhookEvent(payload, sigHeader);
            return ResponseEntity.ok(result);
        } catch (PaymentProcessingException e) {
            log.error("Error al procesar webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al procesar webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error inesperado al procesar el webhook");
        }
    }
}