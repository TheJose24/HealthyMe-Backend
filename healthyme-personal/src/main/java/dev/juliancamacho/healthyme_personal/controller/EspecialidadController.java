package dev.juliancamacho.healthyme_personal.controller;

import dev.juliancamacho.healthyme_personal.dto.EspecialidadDto;
import dev.juliancamacho.healthyme_personal.service.interfaces.EspecialidadService;
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
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
@Tag(name = "Especialidades",
        description = "API para gestionar especialidades")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    // CREATE
    @Operation(
            summary = "Crear una nueva especialidad médica",
            description = "Registra una nueva especialidad en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Especialidad creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PostMapping()
    public ResponseEntity<EspecialidadDto> createEspecialidad(@Valid @RequestBody EspecialidadDto especialidadDto) {
        return new ResponseEntity<>(especialidadService.createEspecialidad(especialidadDto), HttpStatus.CREATED);
    }

    // SELECT BY ID
    @Operation(
            summary = "Obtener especialidad por ID",
            description = "Retorna una lista de todas las especialidades segun su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Especialidad encontrada"),
                    @ApiResponse(responseCode = "404", description = "Especialidad no encontrada")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDto> getEspecialidadById(@PathVariable Integer id) {
        return new ResponseEntity<>(especialidadService.getEspecialidadById(id), HttpStatus.OK);
    }

    // SELECT ALL
    @Operation(
            summary = "Obtener todas las especialidades médicas",
            description = "Retorna una lista de todas las especialidades registradas en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de especialidades obtenida exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No hay especialidades registradas")
            }
    )
    @GetMapping()
    public ResponseEntity<List<EspecialidadDto>> getAllEspecialidades() {
        List<EspecialidadDto> especialidades = especialidadService.getAllEspecialidades();
        if (especialidades.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 si no hay datos
        }
        return new ResponseEntity<>(especialidades, HttpStatus.OK);
    }

    // UPDATE
    @Operation(
            summary = "Actualizar una especialidad médica",
            description = "Actualiza los datos de una especialidad existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Especialidad actualizada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Especialidad no encontrada"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadDto> updateEspecialidad(@PathVariable Integer id, @Valid @RequestBody EspecialidadDto especialidadDto) {
        return new ResponseEntity<>(especialidadService.updateEspecialidad(id, especialidadDto), HttpStatus.OK);
    }

    // DELETE
    @Operation(
            summary = "Eliminar una especialidad médica",
            description = "Elimina una especialidad existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Especialidad eliminada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Especialidad no encontrada")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEspecialidadById(@PathVariable Integer id) {
        especialidadService.deleteEspecialidadById(id);
        return ResponseEntity.ok("Especialidad eliminada con éxito");
    }
}
