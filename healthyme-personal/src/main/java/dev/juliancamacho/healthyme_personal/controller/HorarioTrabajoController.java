package dev.juliancamacho.healthyme_personal.controller;

import dev.juliancamacho.healthyme_personal.dto.HorarioTrabajoDto;
import dev.juliancamacho.healthyme_personal.service.interfaces.HorarioTrabajoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horario-trabajo")
@RequiredArgsConstructor
@Tag(name = "Horario de Trabajo",
        description = "API para gestionar horario de trabajo")
public class HorarioTrabajoController {

    private final HorarioTrabajoService horarioTrabajoService;

    // CREATE
    @Operation(
            summary = "Crear un nuevo horario de trabajo médico",
            description = "Registra un nuevo horario de trabajo en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Horario de Trabajo creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PostMapping()
    public ResponseEntity<HorarioTrabajoDto> createHorarioTrabajo(@Valid @RequestBody HorarioTrabajoDto horarioTrabajoDto) {
        return new ResponseEntity<>(horarioTrabajoService.createHorarioTrabajo(horarioTrabajoDto), HttpStatus.CREATED);
    }

    // SELECT BY ID
    @Operation(
            summary = "Obtener horario de trabajo por ID",
            description = "Retorna una lista de todos los horario de trabajo segun su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Horario de Trabajo encontrado"),
                    @ApiResponse(responseCode = "404", description = "Horario de Trabajo no encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<HorarioTrabajoDto> getHorarioTrabajoById(@PathVariable Integer id) {
        return new ResponseEntity<>(horarioTrabajoService.getHorarioTrabajoById(id), HttpStatus.OK);
    }

    // SELECT ALL
    @Operation(
            summary = "Obtener todos los horario de trabajos médicos",
            description = "Retorna una lista de todos los horario de trabajo registrados en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de horario de trabajo obtenida exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No hay horario de trabajo registrados")
            }
    )
    @GetMapping()
    public ResponseEntity<List<HorarioTrabajoDto>> getAllHorarioTrabajo() {
        List<HorarioTrabajoDto> horarioTrabajoes = horarioTrabajoService.getAllHorarioTrabajo();
        if (horarioTrabajoes.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 si no hay datos
        }
        return new ResponseEntity<>(horarioTrabajoes, HttpStatus.OK);
    }

    // UPDATE
    @Operation(
            summary = "Actualizar una horario de trabajo médico",
            description = "Actualiza los datos de una horario de trabajo existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Horario de Trabajo actualizada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Horario de Trabajo no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<HorarioTrabajoDto> updateHorarioTrabajo(@PathVariable Integer id, @Valid @RequestBody HorarioTrabajoDto horarioTrabajoDto) {
        return new ResponseEntity<>(horarioTrabajoService.updateHorarioTrabajo(id, horarioTrabajoDto), HttpStatus.OK);
    }

    // DELETE
    @Operation(
            summary = "Eliminar una horario de trabajo médico",
            description = "Elimina una horario de trabajo existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Horario de Trabajo eliminado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Horario de Trabajo no encontrado")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHorarioTrabajoById(@PathVariable Integer id) {
        horarioTrabajoService.deleteHorarioTrabajoById(id);
        return new ResponseEntity<>("Horario de trabajo eliminado con éxito", HttpStatus.OK);
    }
}
