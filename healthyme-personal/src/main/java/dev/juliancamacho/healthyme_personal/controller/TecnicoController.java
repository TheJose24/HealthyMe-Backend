package dev.juliancamacho.healthyme_personal.controller;


import dev.juliancamacho.healthyme_personal.dto.TecnicoDto;
import dev.juliancamacho.healthyme_personal.service.interfaces.TecnicoService;
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
@RequestMapping("/api/tecnicos")
@RequiredArgsConstructor
@Tag(name = "Tecnicos",
        description = "API para gestionar tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    // CREATE
    @Operation(
            summary = "Crear un tecnico",
            description = "Registra un nuevo tecnico en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tecnico creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PostMapping()
    public ResponseEntity<TecnicoDto> createTecnico(@Valid @RequestBody TecnicoDto tecnicoDto) {
        return new ResponseEntity<>(tecnicoService.createTecnico(tecnicoDto), HttpStatus.CREATED);
    }

    // SELECT BY ID
    @Operation(
            summary = "Obtener tecnico por ID",
            description = "Retorna una lista de todos los tecnicos segun su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tecnico encontrado"),
                    @ApiResponse(responseCode = "404", description = "Tecnico no encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TecnicoDto> getTecnicoById(@PathVariable Integer id) {
        return new ResponseEntity<>(tecnicoService.getTecnicoById(id), HttpStatus.OK);
    }

    // SELECT ALL
    @Operation(
            summary = "Obtener todos los tecnico",
            description = "Retorna una lista de todos los tecnicos registrados en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de tecnicos obtenida exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No hay tecnicos registradas")
            }
    )
    @GetMapping()
    public ResponseEntity<List<TecnicoDto>> getAllTecnico() {
        List<TecnicoDto> tecnico = tecnicoService.getAllTecnico();
        if (tecnico.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 si no hay datos
        }
        return new ResponseEntity<>(tecnico, HttpStatus.OK);
    }

    // UPDATE
    @Operation(
            summary = "Actualizar una tecnico",
            description = "Actualiza los datos de una tecnico existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tecnico actualizadao exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Tecnico no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TecnicoDto> updateTecnico(@PathVariable Integer id, @Valid @RequestBody TecnicoDto tecnicoDto) {
        return new ResponseEntity<>(tecnicoService.updateTecnico(id, tecnicoDto), HttpStatus.OK);
    }

    // DELETE
    @Operation(
            summary = "Eliminar una tecnico",
            description = "Elimina una tecnico existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tecnico eliminado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Tecnico no encontrado")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTecnicoById(@PathVariable Integer id) {
        tecnicoService.deleteTecnicoById(id);
        return ResponseEntity.ok("Tecnico eliminado con éxito");
    }
}
