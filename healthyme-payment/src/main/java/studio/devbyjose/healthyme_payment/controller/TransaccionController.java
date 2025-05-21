package studio.devbyjose.healthyme_payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_payment.dto.CreateTransaccionDTO;
import studio.devbyjose.healthyme_payment.dto.TransaccionDTO;
import studio.devbyjose.healthyme_payment.service.interfaces.TransaccionService;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;

    @PostMapping
    public ResponseEntity<TransaccionDTO> createTransaccion(@Valid @RequestBody CreateTransaccionDTO createTransaccionDTO) {
        TransaccionDTO resultado = transaccionService.createTransaccion(createTransaccionDTO);
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionDTO> getTransaccionById(@PathVariable Integer id) {
        TransaccionDTO transaccionDTO = transaccionService.getTransaccionById(id);
        return ResponseEntity.ok(transaccionDTO);
    }

    @GetMapping("/pago/{idPago}")
    public ResponseEntity<List<TransaccionDTO>> getTransaccionesByPago(@PathVariable Integer idPago) {
        List<TransaccionDTO> transacciones = transaccionService.getTransaccionesByPago(idPago);
        return ResponseEntity.ok(transacciones);
    }

    @GetMapping("/referencia-externa/{referenciaExterna}")
    public ResponseEntity<TransaccionDTO> getTransaccionByReferenciaExterna(@PathVariable String referenciaExterna) {
        TransaccionDTO transaccionDTO = transaccionService.getTransaccionByReferenciaExterna(referenciaExterna);
        return ResponseEntity.ok(transaccionDTO);
    }
}