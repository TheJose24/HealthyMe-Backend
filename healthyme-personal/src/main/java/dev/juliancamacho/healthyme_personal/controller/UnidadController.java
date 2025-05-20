package dev.juliancamacho.healthyme_personal.controller;

import dev.juliancamacho.healthyme_personal.dto.UnidadDto;
import dev.juliancamacho.healthyme_personal.service.interfaces.UnidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades Médicas",
        description = "API para gestionar unidades médicas")
public class UnidadController {

    private final UnidadService unidadService;

    // CREATE
    @Operation(
            summary = "Crear una nueva unidad médica",
            description = "Registra una nueva unidad en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Unidad creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PostMapping()
    public ResponseEntity<UnidadDto> createUnidada(@Valid @RequestBody UnidadDto unidadDto) {
        return new ResponseEntity<>(unidadService.createUnidad(unidadDto), HttpStatus.CREATED);
    }

    // SELECT BY ID
    @Operation(
            summary = "Obtener unidad por ID",
            description = "Retorna una lista de todas las unidades segun su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unidad encontrada"),
                    @ApiResponse(responseCode = "404", description = "Unidad no encontrada")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UnidadDto> getUnidadaById(@PathVariable Integer id) {
        return new ResponseEntity<>(unidadService.getUnidadById(id), HttpStatus.OK);
    }

    // SELECT ALL
    @Operation(
            summary = "Obtener todas las unidades médicas",
            description = "Retorna una lista de todas las unidades registradas en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de unidades obtenida exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No hay unidades registradas")
            }
    )
    @GetMapping()
    public ResponseEntity<List<UnidadDto>> getAllUnidadaes() {
        List<UnidadDto> unidades = unidadService.getAllUnidades();
        if (unidades.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 si no hay datos
        }
        return new ResponseEntity<>(unidades, HttpStatus.OK);
    }

    // UPDATE
    @Operation(
            summary = "Actualizar una unidad médica",
            description = "Actualiza los datos de una unidad existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unidad actualizada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Unidad no encontrada"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UnidadDto> updateUnidada(@PathVariable Integer id, @Valid @RequestBody UnidadDto unidadDto) {
        return new ResponseEntity<>(unidadService.updateUnidad(id, unidadDto), HttpStatus.OK);
    }

    // DELETE
    @Operation(
            summary = "Eliminar una unidad médica",
            description = "Elimina una unidad existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unidad eliminada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Unidad no encontrada")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUnidadaById(@PathVariable Integer id) {
        unidadService.deleteUnidadById(id);
        return ResponseEntity.ok("Unidad eliminada con éxito");
    }
}
