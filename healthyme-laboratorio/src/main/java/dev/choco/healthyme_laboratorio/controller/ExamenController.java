package dev.choco.healthyme_laboratorio.controller;

import dev.choco.healthyme_laboratorio.dto.ExamenDTO;
import dev.choco.healthyme_laboratorio.service.Interfaces.ExamenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examenes")
@RequiredArgsConstructor
@Tag(name = "Exámenes de Laboratorio", description = "Gestión de exámenes realizados en reservas de laboratorio")

public class ExamenController {
    private final ExamenService service;

    @Operation(
            summary = "Registrar nuevo examen",
            description = "Registra un examen realizado asociado a una reserva de laboratorio"
    )
    @PostMapping
    public ResponseEntity<ExamenDTO> guardar(@Valid @RequestBody ExamenDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @Operation(
            summary = "Listar todos los exámenes",
            description = "Obtiene una lista de todos los exámenes registrados en el sistema"
    )
    @GetMapping
    public ResponseEntity<List<ExamenDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(
            summary = "Obtener un examen por ID",
            description = "Obtiene los detalles de un examen específico usando su ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ExamenDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(
            summary = "Actualizar un examen",
            description = "Modifica los datos de un examen ya registrado"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ExamenDTO> actualizar(@PathVariable Integer id,@Valid @RequestBody ExamenDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(
            summary = "Eliminar un examen",
            description = "Elimina un examen del sistema por su ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

