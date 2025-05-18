package studio.devbyjose.healthyme_pacientes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_pacientes.dto.TriajeDTO;
import studio.devbyjose.healthyme_pacientes.service.interfaces.TriajeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/triajes")
@RequiredArgsConstructor
@Tag(name = "Triaje Controller", description = "API para la gestión de triajes médicos")
public class TriajeController {
    private final TriajeService triajeService;

    @GetMapping
    @Operation(summary = "Obtener todos los triajes")
    public ResponseEntity<List<TriajeDTO>> findAll() {
        return ResponseEntity.ok(triajeService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un triaje por ID")
    public ResponseEntity<TriajeDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(triajeService.findById(id));
    }

    @GetMapping("/paciente/{idPaciente}")
    @Operation(summary = "Obtener todos los triajes de un paciente ordenados por fecha y hora")
    public ResponseEntity<List<TriajeDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(triajeService.findByPaciente(idPaciente));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo triaje")
    public ResponseEntity<TriajeDTO> create(@Valid @RequestBody TriajeDTO triajeDTO) {
        return new ResponseEntity<>(triajeService.create(triajeDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un triaje existente")
    public ResponseEntity<TriajeDTO> update(@PathVariable Long id, @Valid @RequestBody TriajeDTO triajeDTO) {
        return ResponseEntity.ok(triajeService.update(id, triajeDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un triaje")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        triajeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}