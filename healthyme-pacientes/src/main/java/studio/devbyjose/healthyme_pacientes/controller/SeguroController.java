package studio.devbyjose.healthyme_pacientes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_pacientes.dto.SeguroDTO;
import studio.devbyjose.healthyme_pacientes.service.interfaces.SeguroService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seguros")
@RequiredArgsConstructor
@Tag(name = "Seguro Controller", description = "API para la gestión de seguros médicos")
public class SeguroController {
    private final SeguroService seguroService;

    @GetMapping
    @Operation(summary = "Obtener todos los seguros médicos")
    public ResponseEntity<List<SeguroDTO>> findAll() {
        return ResponseEntity.ok(seguroService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un seguro médico por ID")
    public ResponseEntity<SeguroDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(seguroService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo seguro médico")
    public ResponseEntity<SeguroDTO> create(@Valid @RequestBody SeguroDTO seguroDTO) {
        return new ResponseEntity<>(seguroService.create(seguroDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un seguro médico existente")
    public ResponseEntity<SeguroDTO> update(@PathVariable Long id, @Valid @RequestBody SeguroDTO seguroDTO) {
        return ResponseEntity.ok(seguroService.update(id, seguroDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un seguro médico")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seguroService.delete(id);
        return ResponseEntity.noContent().build();
    }
}