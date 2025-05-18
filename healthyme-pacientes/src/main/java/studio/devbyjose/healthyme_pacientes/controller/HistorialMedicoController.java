package studio.devbyjose.healthyme_pacientes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_pacientes.dto.HistorialMedicoDTO;
import studio.devbyjose.healthyme_pacientes.service.interfaces.HistorialMedicoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historiales")
@RequiredArgsConstructor
@Tag(name = "Historial Médico Controller", description = "API para la gestión de historiales médicos")
public class HistorialMedicoController {
    private final HistorialMedicoService historialService;

    @GetMapping
    @Operation(summary = "Obtener todos los historiales médicos")
    public ResponseEntity<List<HistorialMedicoDTO>> findAll() {
        return ResponseEntity.ok(historialService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un historial médico por ID")
    public ResponseEntity<HistorialMedicoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(historialService.findById(id));
    }

    @GetMapping("/paciente/{idPaciente}")
    @Operation(summary = "Obtener todos los historiales médicos de un paciente")
    public ResponseEntity<List<HistorialMedicoDTO>> findByPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(historialService.findByPaciente(idPaciente));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo historial médico")
    public ResponseEntity<HistorialMedicoDTO> create(@Valid @RequestBody HistorialMedicoDTO historialDTO) {
        return new ResponseEntity<>(historialService.create(historialDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un historial médico existente")
    public ResponseEntity<HistorialMedicoDTO> update(@PathVariable Long id, @Valid @RequestBody HistorialMedicoDTO historialDTO) {
        return ResponseEntity.ok(historialService.update(id, historialDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un historial médico")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        historialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}