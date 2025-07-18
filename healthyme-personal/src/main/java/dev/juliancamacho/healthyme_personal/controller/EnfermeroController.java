package dev.juliancamacho.healthyme_personal.controller;


import dev.juliancamacho.healthyme_personal.dto.EnfermeroDto;
import dev.juliancamacho.healthyme_personal.service.interfaces.EnfermeroService;
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
@RequestMapping("/api/v1/enfermeros")
@RequiredArgsConstructor
@Tag(name = "Enfermeros",
        description = "API para gestionar enfermeros")
public class EnfermeroController {

    private final EnfermeroService enfermeroService;

    // CREATE
    @Operation(
            summary = "Crear un enfermero",
            description = "Registra un nuevo enfermero en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Enfermero creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PostMapping()
    public ResponseEntity<EnfermeroDto> createEnfermero(@Valid @RequestBody EnfermeroDto enfermeroDto) {
        return new ResponseEntity<>(enfermeroService.createEnfermero(enfermeroDto), HttpStatus.CREATED);
    }

    // SELECT BY ID
    @Operation(
            summary = "Obtener enfermero por ID",
            description = "Retorna una lista de todos los enfermeros segun su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Enfermero encontrado"),
                    @ApiResponse(responseCode = "404", description = "Enfermero no encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<EnfermeroDto> getEnfermeroById(@PathVariable Integer id) {
        return new ResponseEntity<>(enfermeroService.getEnfermeroById(id), HttpStatus.OK);
    }

    // SELECT ALL
    @Operation(
            summary = "Obtener todos los enfermero",
            description = "Retorna una lista de todos los enfermeros registrados en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de enfermeros obtenida exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No hay enfermeros registradas")
            }
    )
    @GetMapping()
    public ResponseEntity<List<EnfermeroDto>> getAllEnfermero() {
        List<EnfermeroDto> enfermero = enfermeroService.getAllEnfermero();
        if (enfermero.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 si no hay datos
        }
        return new ResponseEntity<>(enfermero, HttpStatus.OK);
    }

    // UPDATE
    @Operation(
            summary = "Actualizar una enfermero",
            description = "Actualiza los datos de una enfermero existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Enfermero actualizadao exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Enfermero no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<EnfermeroDto> updateEnfermero(@PathVariable Integer id, @Valid @RequestBody EnfermeroDto enfermeroDto) {
        return new ResponseEntity<>(enfermeroService.updateEnfermero(id, enfermeroDto), HttpStatus.OK);
    }

    // DELETE
    @Operation(
            summary = "Eliminar una enfermero",
            description = "Elimina una enfermero existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Enfermero eliminado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Enfermero no encontrado")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEnfermeroById(@PathVariable Integer id) {
        enfermeroService.deleteEnfermeroById(id);
        return ResponseEntity.ok("Enfermero eliminado con éxito");
    }
}

