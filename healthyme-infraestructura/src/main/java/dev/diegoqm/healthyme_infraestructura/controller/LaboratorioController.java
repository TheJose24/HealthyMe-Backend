package dev.diegoqm.healthyme_infraestructura.controller;

import dev.diegoqm.healthyme_infraestructura.dto.LaboratorioDTO;
import dev.diegoqm.healthyme_infraestructura.service.interfaces.LaboratorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Laboratorios", description = "API para gestionar laboratorios")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/laboratorios")
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    @Operation(summary = "Crear un nuevo laboratorio")
    @PostMapping
    public ResponseEntity<LaboratorioDTO> createLaboratorio(@Valid @RequestBody LaboratorioDTO dto) {
        return new ResponseEntity<>(laboratorioService.createLaboratorio(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un laboratorio por ID")
    @GetMapping("/{id}")
    public ResponseEntity<LaboratorioDTO> getLaboratorioById(@PathVariable int id) {
        return new ResponseEntity<>(laboratorioService.getLaboratorioById(id), HttpStatus.OK);
    }

    @Operation(summary = "Obtener la lista de todos los laboratorios")
    @GetMapping
    public ResponseEntity<List<LaboratorioDTO>> getAllLaboratorios() {
        return new ResponseEntity<>(laboratorioService.getAllLaboratorios(), HttpStatus.OK);
    }

    @Operation(summary = "Actualizar un laboratorio existente")
    @PutMapping("/{id}")
    public ResponseEntity<LaboratorioDTO> updateLaboratorio(@PathVariable int id,
                                                            @Valid @RequestBody LaboratorioDTO dto) {
        return new ResponseEntity<>(laboratorioService.updateLaboratorio(id, dto), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un laboratorio por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLaboratorioById(@PathVariable int id) {
        laboratorioService.deleteLaboratorioById(id);
        return new ResponseEntity<>("Laboratorio eliminado con éxito", HttpStatus.OK);
    }
}