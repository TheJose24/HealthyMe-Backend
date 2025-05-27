package dev.Elmer.healthyme_consultas.controller;

import dev.Elmer.healthyme_consultas.dto.RecetaDto;
import dev.Elmer.healthyme_consultas.entity.Receta;
import dev.Elmer.healthyme_consultas.exception.RecetaNotFoundException;
import dev.Elmer.healthyme_consultas.repository.RecetaRepository;
import dev.Elmer.healthyme_consultas.service.interfaces.PdfService;
import dev.Elmer.healthyme_consultas.service.interfaces.RecetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
@Tag(name = "Recetas", description = "Gestión de Recetas Médicas")
public class RecetaController {

    private final RecetaService service;
    private final RecetaRepository recetaRepository;
    private final PdfService pdfService;

    @Operation(summary = "Crear nueva receta", description = "Guarda una nueva receta médica asociada a una consulta.")
    @ApiResponse(responseCode = "201", description = "Receta creada exitosamente")
    @PostMapping
    public ResponseEntity<RecetaDto> guardar(@Valid @RequestBody RecetaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @Operation(summary = "Listar recetas", description = "Obtiene una lista de todas las recetas médicas registradas.")
    @GetMapping
    public ResponseEntity<List<RecetaDto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Obtener receta por ID", description = "Busca una receta médica por su identificador único.")
    @ApiResponse(responseCode = "200", description = "Receta encontrada")
    @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<RecetaDto> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Actualizar receta", description = "Actualiza los datos de una receta médica existente.")
    @PutMapping("/{id}")
    public ResponseEntity<RecetaDto> actualizar(@PathVariable Integer id, @Valid @RequestBody RecetaDto dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar receta", description = "Elimina una receta médica por su ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generarPdf(@PathVariable Integer id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RecetaNotFoundException("Receta no encontrado"));

        byte[] pdfBytes = pdfService.generarPdfExamen(receta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receta-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}