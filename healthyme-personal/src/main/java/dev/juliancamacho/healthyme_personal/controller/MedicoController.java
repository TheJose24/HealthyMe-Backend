package dev.juliancamacho.healthyme_personal.controller;

import dev.juliancamacho.healthyme_personal.dto.MedicoDto;
import dev.juliancamacho.healthyme_personal.service.interfaces.MedicoService;
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
@RequestMapping("/api/v1/medicos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Medicos",
        description = "API para gestionar medicos")
public class MedicoController {

    private final MedicoService medicoService;

    // CREATE
    @Operation(
            summary = "Crear un medico",
            description = "Registra un nuevo medico en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Medico creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PostMapping()
    public ResponseEntity<MedicoDto> createMedico(@Valid @RequestBody MedicoDto medicoDto) {
        return new ResponseEntity<>(medicoService.createMedico(medicoDto), HttpStatus.CREATED);
    }

    // SELECT BY ID
    @Operation(
            summary = "Obtener medico por ID",
            description = "Retorna una lista de todos los medicos segun su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Medico encontrado"),
                    @ApiResponse(responseCode = "404", description = "Medico no encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<MedicoDto> getMedicoById(@PathVariable Integer id) {
        return new ResponseEntity<>(medicoService.getMedicoById(id), HttpStatus.OK);
    }

    // SELECT ALL
    @Operation(
            summary = "Obtener todos los medico",
            description = "Retorna una lista de todos los medicos registrados en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de medicos obtenida exitosamente"),
                    @ApiResponse(responseCode = "204", description = "No hay medicos registradas")
            }
    )
    @GetMapping()
    public ResponseEntity<List<MedicoDto>> getAllMedico() {
        List<MedicoDto> medico = medicoService.getAllMedico();
        if (medico.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 si no hay datos
        }
        return new ResponseEntity<>(medico, HttpStatus.OK);
    }

    // UPDATE
    @Operation(
            summary = "Actualizar una medico",
            description = "Actualiza los datos de una medico existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Medico actualizadao exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Medico no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<MedicoDto> updateMedico(@PathVariable Integer id, @Valid @RequestBody MedicoDto medicoDto) {
        return new ResponseEntity<>(medicoService.updateMedico(id, medicoDto), HttpStatus.OK);
    }

    // DELETE
    @Operation(
            summary = "Eliminar una medico",
            description = "Elimina una medico existente usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Medico eliminado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Medico no encontrado")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedicoById(@PathVariable Integer id) {
        medicoService.deleteMedicoById(id);
        return ResponseEntity.ok("Medico eliminado con éxito");
    }

    // COUNT
    @GetMapping("/count")
    @Operation(summary = "Obtener el número total de médicos")
    public ResponseEntity<Long> getTotalMedicos() {
        return ResponseEntity.ok(medicoService.countMedicos());
    }
}
