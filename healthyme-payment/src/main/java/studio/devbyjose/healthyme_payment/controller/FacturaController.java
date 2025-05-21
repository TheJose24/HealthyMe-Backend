package studio.devbyjose.healthyme_payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_commons.client.dto.FacturaDTO;
import studio.devbyjose.healthyme_payment.dto.CreateFacturaDTO;
import studio.devbyjose.healthyme_payment.service.interfaces.FacturaService;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;

    @PostMapping
    public ResponseEntity<FacturaDTO> createFactura(@Valid @RequestBody CreateFacturaDTO createFacturaDTO) {
        FacturaDTO resultado = facturaService.createFactura(createFacturaDTO);
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO> getFacturaById(@PathVariable Integer id) {
        FacturaDTO facturaDTO = facturaService.getFacturaById(id);
        return ResponseEntity.ok(facturaDTO);
    }

    @GetMapping("/numero/{numeroFactura}")
    public ResponseEntity<FacturaDTO> getFacturaByNumero(@PathVariable String numeroFactura) {
        FacturaDTO facturaDTO = facturaService.getFacturaByNumero(numeroFactura);
        return ResponseEntity.ok(facturaDTO);
    }

    @GetMapping("/pago/{idPago}")
    public ResponseEntity<FacturaDTO> getFacturaByPago(@PathVariable Integer idPago) {
        FacturaDTO facturaDTO = facturaService.getFacturaByPago(idPago);
        return ResponseEntity.ok(facturaDTO);
    }

    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<String> sendFacturaByEmail(@PathVariable Integer id) {
        boolean enviado = facturaService.sendFacturaByEmail(id);
        if (enviado) {
            return ResponseEntity.ok("Factura enviada por correo electrónico correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo enviar la factura por correo electrónico");
        }
    }
}