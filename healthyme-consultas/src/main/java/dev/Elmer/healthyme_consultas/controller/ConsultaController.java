package dev.Elmer.healthyme_consultas.controller;

import dev.Elmer.healthyme_consultas.dto.ConsultaDto;
import dev.Elmer.healthyme_consultas.service.interfaces.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas", description = "Gestión de Consultas Médicas")
public class ConsultaController {

    private final ConsultaService service;

    @Operation(summary = "Crear nueva consulta", description = "Guarda una nueva consulta médica.")
    @ApiResponse(responseCode = "201", description = "Consulta creada e xitosamente")
    @PostMapping
    public ResponseEntity<ConsultaDto> guardar(@Valid @RequestBody ConsultaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @Operation(summary = "Listar consultas", description = "Obtiene una lista de todas las consultas médicas registradas.")
    @GetMapping
    public ResponseEntity<List<ConsultaDto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Obtener consulta por ID", description = "Busca una consulta médica por su identificador único.")
    @ApiResponse(responseCode = "200", description = "Consulta encontrada")
    @ApiResponse(responseCode = "404", description = "Consulta no encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDto> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Actualizar consulta", description = "Actualiza los datos de una consulta médica existente.")
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaDto> actualizar(@PathVariable Integer id, @Valid @RequestBody ConsultaDto dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar consulta", description = "Elimina una consulta médica por su ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
