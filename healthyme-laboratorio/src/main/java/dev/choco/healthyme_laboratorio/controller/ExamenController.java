package dev.choco.healthyme_laboratorio.controller;

import dev.choco.healthyme_laboratorio.dto.ExamenDTO;
import dev.choco.healthyme_laboratorio.service.Interfaces.ExamenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examenes")
@RequiredArgsConstructor
public class ExamenController {
    private final ExamenService service;

    @PostMapping
    public ResponseEntity<ExamenDTO> guardar(@RequestBody ExamenDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ExamenDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamenDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamenDTO> actualizar(@PathVariable Integer id, @RequestBody ExamenDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

