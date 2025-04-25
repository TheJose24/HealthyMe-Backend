package dev.Elmer.healthyme_consultas.controller;

import dev.Elmer.healthyme_consultas.dto.RecetaDto;
import dev.Elmer.healthyme_consultas.service.interfaces.RecetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService service;

    @PostMapping
    public ResponseEntity<RecetaDto> guardar(@RequestBody RecetaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @GetMapping
    public ResponseEntity<List<RecetaDto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaDto> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecetaDto> actualizar(@PathVariable Integer id, @RequestBody RecetaDto dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}