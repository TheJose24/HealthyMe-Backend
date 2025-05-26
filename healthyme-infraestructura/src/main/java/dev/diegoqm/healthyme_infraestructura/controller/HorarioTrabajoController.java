package dev.diegoqm.healthyme_infraestructura.controller;

import dev.diegoqm.healthyme_infraestructura.dto.HorarioTrabajoDTO;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.HorarioTrabajoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Horario de Trabajo", description = "API para gestionar horarios de trabajo")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/horario-trabajo")
public class HorarioTrabajoController {

    private final HorarioTrabajoService horarioTrabajoService;

    @Operation(summary = "Crear un nuevo horario de trabajo")
    @PostMapping
    public ResponseEntity<HorarioTrabajoDTO> createHorarioTrabajo(@Valid @RequestBody HorarioTrabajoDTO dto) {
        return new ResponseEntity<>(horarioTrabajoService.createHorarioTrabajo(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un horario de trabajo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<HorarioTrabajoDTO> getHorarioTrabajoById(@PathVariable int id) {
        return new ResponseEntity<>(horarioTrabajoService.getHorarioTrabajoById(id), HttpStatus.OK);
    }

    @Operation(summary = "Obtener la lista de todos los horarios de trabajo")
    @GetMapping
    public ResponseEntity<List<HorarioTrabajoDTO>> getAllHorarioTrabajo() {
        return new ResponseEntity<>(horarioTrabajoService.getAllHorarioTrabajo(), HttpStatus.OK);
    }

    @Operation(summary = "Actualizar un horario de trabajo")
    @PutMapping("/{id}")
    public ResponseEntity<HorarioTrabajoDTO> updateHorarioTrabajo(@PathVariable int id,
                                                                  @Valid @RequestBody HorarioTrabajoDTO dto) {
        return new ResponseEntity<>(horarioTrabajoService.updateHorarioTrabajo(id, dto), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un horario de trabajo por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHorarioTrabajoById(@PathVariable int id) {
        horarioTrabajoService.deleteHorarioTrabajoById(id);
        return new ResponseEntity<>("Horario eliminado con éxito", HttpStatus.OK);
    }
}