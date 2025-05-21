package studio.devbyjose.healthyme_payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_commons.client.dto.MetodoPagoDTO;
import studio.devbyjose.healthyme_payment.dto.CreateMetodoPagoDTO;
import studio.devbyjose.healthyme_payment.service.interfaces.MetodoPagoService;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
@RequiredArgsConstructor
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    @PostMapping
    public ResponseEntity<MetodoPagoDTO> createMetodoPago(@Valid @RequestBody CreateMetodoPagoDTO createMetodoPagoDTO) {
        MetodoPagoDTO resultado = metodoPagoService.createMetodoPago(createMetodoPagoDTO);
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagoDTO> getMetodoPagoById(@PathVariable Integer id) {
        MetodoPagoDTO metodoPagoDTO = metodoPagoService.getMetodoPagoById(id);
        return ResponseEntity.ok(metodoPagoDTO);
    }

    @GetMapping
    public ResponseEntity<List<MetodoPagoDTO>> getAllMetodoPagos() {
        List<MetodoPagoDTO> metodoPagos = metodoPagoService.getAllMetodoPagos();
        return ResponseEntity.ok(metodoPagos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<MetodoPagoDTO>> getMetodoPagosActivos() {
        List<MetodoPagoDTO> metodoPagosActivos = metodoPagoService.getAllMetodoPagosActivos();
        return ResponseEntity.ok(metodoPagosActivos);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<MetodoPagoDTO> updateEstadoMetodoPago(
            @PathVariable Integer id, 
            @RequestParam Boolean estado) {
        MetodoPagoDTO metodoPagoActualizado = metodoPagoService.updateEstadoMetodoPago(id, estado);
        return ResponseEntity.ok(metodoPagoActualizado);
    }
}