package dev.choco.healthyme_laboratorio.controller;

import dev.choco.healthyme_laboratorio.dto.ReservaLabDTO;
import dev.choco.healthyme_laboratorio.service.Interfaces.ReservaLabService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaLabController {

    private final ReservaLabService service;

    @PostMapping
    public ResponseEntity<ReservaLabDTO> guardar(@RequestBody ReservaLabDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ReservaLabDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaLabDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaLabDTO> actualizar(@PathVariable Integer id, @RequestBody ReservaLabDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
