package studio.devbyjose.healthyme_pacientes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.devbyjose.healthyme_pacientes.dto.PacienteDTO;
import studio.devbyjose.healthyme_pacientes.service.interfaces.PacienteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
@RequiredArgsConstructor
@Tag(name = "Paciente Controller", description = "API para la gestión de pacientes")
public class PacienteController {
    private final PacienteService pacienteService;

    @GetMapping
    @Operation(summary = "Obtener todos los pacientes")
    public ResponseEntity<List<PacienteDTO>> findAll() {
        return ResponseEntity.ok(pacienteService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un paciente por ID")
    public ResponseEntity<PacienteDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.findById(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Obtener un paciente por ID de usuario")
    public ResponseEntity<PacienteDTO> findByIdUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(pacienteService.findByIdUsuario(idUsuario));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo paciente")
    public ResponseEntity<PacienteDTO> create(@Valid @RequestBody PacienteDTO pacienteDTO) {
        return new ResponseEntity<>(pacienteService.create(pacienteDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un paciente existente")
    public ResponseEntity<PacienteDTO> update(@PathVariable Long id, @Valid @RequestBody PacienteDTO pacienteDTO) {
        return ResponseEntity.ok(pacienteService.update(id, pacienteDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un paciente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}